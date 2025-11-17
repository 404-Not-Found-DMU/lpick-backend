package com.notfound.lpickbackend.servicedata.query.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import com.notfound.lpickbackend.community.query.dto.ArticleListResponse;
import com.notfound.lpickbackend.community.query.dto.PopularArticleResponse;
import com.notfound.lpickbackend.community.query.repository.ArticleQueryRepository;
import com.notfound.lpickbackend.servicedata.query.repository.AlbumQueryRepository;
import com.notfound.lpickbackend.servicedata.query.repository.ArtistQueryRepository;
import com.notfound.lpickbackend.servicedata.query.repository.GearQueryRepository;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wiki.query.dto.response.PopularItemResponse;
import com.notfound.lpickbackend.wiki.query.repository.WikiPageQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopularityService {

    private final ElasticsearchOperations elasticsearchOperations;

    private final AlbumQueryRepository albumRepository;
    private final ArtistQueryRepository artistRepository;
    private final GearQueryRepository gearQueryRepository;
    private final WikiPageQueryRepository wikiPageQueryRepository;
    private final ArticleQueryRepository articleQueryRepository;

    private static final String VIEW_LOG_INDEX = "lpick-views-*";
    private static final String WIKI_AGG_NAME = "popular_wikis_agg";
    private static final String ARTICLE_AGG_NAME = "popular_articles_agg";
    private static final List<String> ALL_TYPES = List.of("ALBUM", "ARTIST", "GEAR");

    public List<PopularItemResponse> getPopularWikis(String type, int size) {

        Query esQuery;

        if(type.equals("ALL")) {
            esQuery = Query.of(q -> q
                    .bool(b -> b
                            .filter(f -> f.terms(t -> t
                                    .field("type.keyword")
                                    .terms(v -> v.value(ALL_TYPES.stream()
                                            .map(FieldValue::of)
                                            .toList()))
                            ))
                            .filter(f -> f.range(r -> r
                                    .untyped(u -> u
                                            .field("@timestamp")
                                            .gte(JsonData.of("now-48h"))
                                            .lt(JsonData.of("now"))
                                    )
                            ))
                    )
            );
        } else {
            esQuery = Query.of(q -> q
                    .bool(b -> b
                            .filter(f -> f.term(t -> t
                                    .field("type.keyword")
                                    .value(type)
                            ))
                            .filter(f -> f.range(r -> r
                                    .untyped(u -> u
                                            .field("@timestamp")
                                            .gte(JsonData.of("now-48h"))
                                            .lt(JsonData.of("now"))
                                    )
                            ))
                    )
            );
        }

        // 2) 집계: AggregateOrder 빌더 사용
        Aggregation esAggregation = Aggregation.of(a -> a
                .terms(t -> t
                        .field("id.keyword")
                        .size(size)
                )
        );

        // 3) NativeQuery 구성 (인덱스는 search 호출에서 지정)
        NativeQuery query = NativeQuery.builder()
                .withQuery(esQuery)
                .withAggregation(WIKI_AGG_NAME, esAggregation)
                .withMaxResults(0)
                .build();

        // 4) 인덱스 지정
        SearchHits<Object> searchHits =
                elasticsearchOperations.search(query, Object.class, IndexCoordinates.of(VIEW_LOG_INDEX));

        log.warn("searchHits {}", searchHits);

        if (searchHits.getAggregations() == null) {
            return Collections.emptyList();
        }

        // 5) AggregationsContainer → asMap() → Aggregate

        var test = searchHits.getAggregations().aggregations();

        @SuppressWarnings("unchecked")
        List<ElasticsearchAggregation> aggList = (List<ElasticsearchAggregation>) searchHits.getAggregations().aggregations();

        if (aggList.isEmpty()) {
            return Collections.emptyList();
        }

        Aggregate aggregate = aggList.getFirst().aggregation().getAggregate();

        StringTermsAggregate popularItemsAgg = aggregate.sterms();

        List<StringTermsBucket> buckets = popularItemsAgg.buckets().array();

        if (buckets.isEmpty()) {
            return Collections.emptyList();
        }

        // 6) String ID 그대로 추출
        List<String> ids = buckets.stream()
                .map(b -> b.key().stringValue())
                .collect(Collectors.toList());
        log.warn("ids : {}", ids);

        Map<String, WikiPage> itemMap = wikiPageQueryRepository.findAllById(ids).stream().collect(Collectors.toMap(WikiPage::getWikiId, Function.identity()));

        // 8) DTO 조합
        return buckets.stream()
                .map(bucket -> {
                    String id = bucket.key().stringValue();
                    long viewCount = bucket.docCount();
                    WikiPage item = itemMap.get(id);
                    return new PopularItemResponse(item, viewCount);
                })
                .filter(obj -> true)
                .collect(Collectors.toList());
    }

    public List<PopularArticleResponse> getPopularArticles(int size) {

        Query esQuery = Query.of(q -> q
                .bool(b -> b
                        .filter(f -> f.term(t -> t
                                .field("type.keyword")
                                .value("ARTICLE")
                        ))
                        .filter(f -> f.range(r -> r
                                .untyped(u -> u
                                        .field("@timestamp")
                                        .gte(JsonData.of("now-48h"))
                                        .lt(JsonData.of("now"))
//                                        .timeZone("Asia/Seoul") // 시간대 지정
                                )
                        ))
                )
        );

        // 2) 집계: AggregateOrder 빌더 사용
        Aggregation esAggregation = Aggregation.of(a -> a
                .terms(t -> t
                        .field("id.keyword")
                        .size(size)
                )
        );

        // 3) NativeQuery 구성 (인덱스는 search 호출에서 지정)
        NativeQuery query = NativeQuery.builder()
                .withQuery(esQuery)
                .withAggregation(ARTICLE_AGG_NAME, esAggregation)
                .withMaxResults(0)
                .build();

        // 4) 인덱스 지정
        SearchHits<Object> searchHits =
                elasticsearchOperations.search(query, Object.class, IndexCoordinates.of(VIEW_LOG_INDEX));

        log.warn("searchHits {}", searchHits);

        if (searchHits.getAggregations() == null) {
            return Collections.emptyList();
        }

        // 5) AggregationsContainer → asMap() → Aggregate

        var test = searchHits.getAggregations().aggregations();

        @SuppressWarnings("unchecked")
        List<ElasticsearchAggregation> aggList = (List<ElasticsearchAggregation>) searchHits.getAggregations().aggregations();

        if (aggList.isEmpty()) {
            return Collections.emptyList();
        }

        Aggregate aggregate = aggList.getFirst().aggregation().getAggregate();

        StringTermsAggregate popularItemsAgg = aggregate.sterms();

        List<StringTermsBucket> buckets = popularItemsAgg.buckets().array();

        log.warn("buckets : {}", buckets);

        if (buckets.isEmpty()) {
            return Collections.emptyList();
        }

        // 6) String ID 그대로 추출
        List<String> ids = buckets.stream()
                .map(b -> b.key().stringValue())
                .collect(Collectors.toList());

        log.warn("ids : {}", ids);

        Map<String, ArticleListResponse> itemMap = articleQueryRepository.findAllWithLikeAndCommentAndBookmarkCountInIds(ids).stream().collect(Collectors.toMap(ArticleListResponse::getArticleId, Function.identity()));

        // 8) DTO 조합
        return buckets.stream()
                .map(bucket -> {
                    String id = bucket.key().stringValue();
                    long viewCount = bucket.docCount();
                    ArticleListResponse item = itemMap.get(id);
                    return new PopularArticleResponse(item, viewCount);
                })
                .filter(obj -> true)
                .collect(Collectors.toList());
    }
}
