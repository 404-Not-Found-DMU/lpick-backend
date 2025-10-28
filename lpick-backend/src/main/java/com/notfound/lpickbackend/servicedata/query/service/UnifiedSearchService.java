package com.notfound.lpickbackend.servicedata.query.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import com.notfound.lpickbackend.common.elasticsearch.document.AlbumDocument;
import com.notfound.lpickbackend.common.elasticsearch.document.ArtistDocument;
import com.notfound.lpickbackend.common.elasticsearch.document.GearDocument;
import com.notfound.lpickbackend.servicedata.query.dto.SearchResult;
import com.notfound.lpickbackend.wiki.query.repository.WikiPageQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@Slf4j
@Service
@RequiredArgsConstructor
public class UnifiedSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final WikiPageQueryRepository wikiPageQueryRepository;

    // 검색 대상 인덱스 목록
    private static final String[] ALL_INDICES = new String[]{
            "wikipages", "articles"
    };

    public List<SearchResult> integratedSearch(String keyword, Pageable pageable) {

        return search(keyword, pageable, ALL_INDICES);
    }

    public List<SearchResult> searchByType(String keyword, Pageable pageable, String[] indices) {

        return search(keyword, pageable, indices);
    }

    private List<SearchResult> search(String keyword, Pageable pageable, String[] INDICES) {
        NativeQuery searchQuery = new NativeQueryBuilder()
                .withQuery(q -> q.multiMatch(m -> m
                        .fields("name", "modelName", "title")
                        .query(keyword)
                        .fuzziness("AUTO")
                        .analyzer("korean_analyzer")
                ))
                .withPageable(pageable)
                .withSort(Sort.by(Sort.Direction.DESC, "_score"))
                .build();

        SearchHits<?> searchHits = elasticsearchOperations.search(
                searchQuery,
                Map.class,
                IndexCoordinates.of(ALL_INDICES) // ✅ 인덱스 여기서 지정
        );

        log.warn(searchHits.toString());

        List<SearchResult> results = new ArrayList<>();

        for (SearchHit<?> hit : searchHits.getSearchHits()) {
            results.add(mapToSearchResult(hit));
        }
        return results;
    }


    public List<SearchResult> autocompleteSuggestions(String prefix, int size) {
        NativeQuery searchQuery = new NativeQueryBuilder()
                .withQuery(q -> q.match(m -> m
                        .field("name")
                        .query(prefix)
                        .operator(Operator.And)
                ))
                .withMaxResults(size)
                .build();

        SearchHits<?> searchHits = elasticsearchOperations.search(
                searchQuery,
                Map.class,
                IndexCoordinates.of(ALL_INDICES)
        );

        log.warn("searchHits: {}", searchHits);

        return searchHits.getSearchHits().stream()
                .map(this::mapToSearchResult)
                .collect(Collectors.toList());
    }


    /**
     * SearchHit을 공통 DTO인 SearchResult로 매핑합니다.
     * **도큐먼트 타입 (Album, Artist, Gear)을 식별하는 것이 이 메서드의 핵심입니다.**
     */
    private SearchResult mapToSearchResult(SearchHit<?> hit) {
        String index = hit.getIndex();
        String id = hit.getId(); // Elasticsearch의 Document ID (@Id 필드 값)
        String wikiId = "";

        // getContent()는 Map<String, Object> 형태의 원시 데이터를 반환합니다.
        // Map으로 안전하게 형 변환합니다.
        @SuppressWarnings("unchecked")
        Map<String, Object> sourceMap = (Map<String, Object>) hit.getContent();

        // 1. 인덱스 이름을 기반으로 도큐먼트 타입 결정
        String documentType;
        String nameValue; // 통합 검색 DTO의 name 필드에 들어갈 값

        if (index.equals("albums")) {
            documentType = AlbumDocument.DOCUMENT_TYPE;
            nameValue = (String) sourceMap.get("name");
        } else if (index.equals("artists")) {
            documentType = ArtistDocument.DOCUMENT_TYPE;
            nameValue = (String) sourceMap.get("name");
        } else if (index.equals("gears")) {
            documentType = GearDocument.DOCUMENT_TYPE;
            // Gear는 modelName으로 검색이 이루어지므로 modelName을 주요 이름으로 사용
            nameValue = (String) sourceMap.get("modelName");
        } else if (index.equals("articles")) {
            documentType = ArtistDocument.DOCUMENT_TYPE;
            // Gear는 modelName으로 검색이 이루어지므로 modelName을 주요 이름으로 사용
            nameValue = (String) sourceMap.get("title");
        } else if (index.equals("wikipages")) {
            documentType = (String) sourceMap.get("wikiClass");
            // Gear는 modelName으로 검색이 이루어지므로 modelName을 주요 이름으로 사용
            nameValue = (String) sourceMap.get("title");
        } else {
            documentType = "OTHER";
            nameValue = "[Unknown Content]";
        }



        return SearchResult.builder()
                .id(id)
                .name(nameValue)
                .documentType(documentType)
                .build();
    }
}
