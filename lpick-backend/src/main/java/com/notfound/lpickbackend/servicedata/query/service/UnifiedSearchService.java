package com.notfound.lpickbackend.servicedata.query.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import com.notfound.lpickbackend.common.elasticsearch.document.AlbumDocument;
import com.notfound.lpickbackend.common.elasticsearch.document.ArtistDocument;
import com.notfound.lpickbackend.common.elasticsearch.document.GearDocument;
import com.notfound.lpickbackend.servicedata.query.dto.GearSearchResultDTO;
import com.notfound.lpickbackend.servicedata.query.dto.SearchResult;
import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.GearClass;
import com.notfound.lpickbackend.wiki.query.repository.WikiPageQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.*;
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
                IndexCoordinates.of(INDICES) // ✅ 인덱스 여기서 지정
        );

        log.warn(searchHits.toString());

        List<SearchResult> results = new ArrayList<>();

        for (SearchHit<?> hit : searchHits.getSearchHits()) {
            results.add(mapToSearchResult(hit));
        }
        return results;
    }

    /**
     * eqClass(TURNTABLE, SPEAKER, HEADPHONE 등)로 필터링된 Gear 검색.
     */
    // Gear 검색 내 정확도 높이기 위해 별도로 분리.
    // 기존 내역은 wiki, gear, article 등의 각각의 엔티티에만 존재하는 field에 대해 전부 score 계산을 해 정확도가 일부 떨어진다... 라는 말이 있네요.
    // 순수하게 gear만 검색할 예정이니 다음과 같이 구현.
    public List<GearSearchResultDTO> searchGears(String keyword,
                                                 Pageable pageable,
                                                 GearClass eqClass) {

        NativeQueryBuilder builder = new NativeQueryBuilder();

        // text(자동 완성 등) 겸 keyword(완벽 매칭) 멀티매치가 가능한 modelName, brand만 사용하여 검색 수행한다!

        // eqClass 필터가 없는 경우: 그냥 multi_match만
        if (eqClass == null) {
            builder.withQuery(q -> q.multiMatch(m -> m
                    .fields("modelName", "brand", "name")
                    .query(keyword)
                    .fuzziness("AUTO")
            ));
        } else {
            builder.withQuery(q -> q.bool(b -> b
                    .must(m -> m.multiMatch(mm -> mm
                            .fields("modelName", "brand", "name")
                            .query(keyword)
                    ))
                    .filter(f -> f.term(t -> t
                            .field("eqClass")       // GearDocument.eqClass
                            .value(eqClass.name())
                    ))
            ));
        }

        NativeQuery searchQuery = builder
                .withPageable(pageable)
                .withSort(Sort.by(Sort.Direction.DESC, "_score"))
                .build();

        SearchHits<GearDocument> searchHits = elasticsearchOperations.search(
                searchQuery,
                GearDocument.class,
                IndexCoordinates.of("gears")
        );

        log.debug("Gear search hits: {}", searchHits);

        List<GearSearchResultDTO> results = new ArrayList<>();

        for (SearchHit<GearDocument> hit : searchHits) {
            results.add(this.mapGearDocumentToResult(hit));
        }

        return results;
    }


    public List<SearchResult> autocompleteSuggestions(String prefix, int size) {
        NativeQuery searchQuery = new NativeQueryBuilder()
                .withQuery(q -> q.multiMatch(m -> m
                        .fields("name", "title", "modelName")
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

    /** gear 자동완성 */
    public List<GearSearchResultDTO> autocompleteGears(String prefix,
                                                       GearClass eqClass,
                                                       int size) {

        NativeQueryBuilder builder = new NativeQueryBuilder();

        if (eqClass == null) {
            builder.withQuery(q -> q.multiMatch(m -> m
                    .fields("modelName", "brand", "name")
                    .query(prefix)
                    .operator(Operator.And)
            ));
        } else {

            builder.withQuery(q -> q.bool(b -> b
                    .must(m -> m.multiMatch(mm -> mm
                            .fields("modelName", "brand", "name")
                            .query(prefix)
                            .operator(Operator.And)
                    ))
                    .filter(f -> f.term(t -> t
                            .field("eqClass")
                            .value(eqClass.name())
                    ))
            ));
        }

        NativeQuery searchQuery = builder
                .withMaxResults(size)
                .build();

        SearchHits<GearDocument> searchHits = elasticsearchOperations.search(
                searchQuery,
                GearDocument.class,
                IndexCoordinates.of("gears")
        );

        log.debug("Gear autocomplete hits: {}", searchHits);

        return searchHits.getSearchHits().stream()
                .map(this::mapGearDocumentToResult)
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

    // --- Gear 전용 매핑 메서드 ---

    private GearSearchResultDTO mapGearDocumentToResult(SearchHit<GearDocument> hit) {
        GearDocument doc = hit.getContent();

        return GearSearchResultDTO.builder()
                .gearId(doc.getGearId())           // pk
                .modelName(doc.getModelName())     // 모델명
                .brand(doc.getBrand())             // 브랜드
                .name(doc.getName())               // 명칭(브랜드 + 모델명 등)
                .img(doc.getImg())       // 이미지 (없다면 null)
                .eqClass(doc.getEqClass())
                .build();
    }


    public List<String> deleteAllIndices(boolean includeSystem) {

        // "*" 를 묶어서 정보만 가져온다 (GET 계열이라 destructive_requires_name 영향 없음)
        IndexOperations wildcardOps =
                elasticsearchOperations.indexOps(IndexCoordinates.of("*"));

        List<IndexInformation> indexInfos = wildcardOps.getInformation();
        List<String> deleted = new ArrayList<>();

        for (IndexInformation info : indexInfos) {
            String indexName = info.getName();

            // 기본값: 시스템 인덱스(.kibana, .security 등)는 보호
            if (!includeSystem && indexName.startsWith(".")) {
                log.info("Skip system index: {}", indexName);
                continue;
            }

            IndexOperations indexOps =
                    elasticsearchOperations.indexOps(IndexCoordinates.of(indexName));

            if (!indexOps.exists()) {
                continue;
            }

            boolean ok = indexOps.delete();   // 명시적 인덱스 이름으로 삭제 → wildcard 제한에 안걸림

            if (ok) {
                deleted.add(indexName);
                log.info("Deleted index: {}", indexName);
            } else {
                log.warn("Failed to delete index: {}", indexName);
            }
        }

        return deleted;
    }
}
