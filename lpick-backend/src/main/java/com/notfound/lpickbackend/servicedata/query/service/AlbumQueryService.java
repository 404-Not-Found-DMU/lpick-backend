package com.notfound.lpickbackend.servicedata.query.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import com.notfound.lpickbackend.common.elasticsearch.document.AlbumDocument;
import com.notfound.lpickbackend.common.elasticsearch.repository.AlbumDocumentRepository;
import com.notfound.lpickbackend.servicedata.command.application.domain.Album;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.servicedata.query.repository.AlbumQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumQueryService {

    private final AlbumQueryRepository albumQueryRepository;
    private final AlbumDocumentRepository albumDocumentRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Transactional(readOnly = true)
    public Album getAlbumById(String albumId) {
        return albumQueryRepository.findById(albumId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ALBUM));
    }

    /**
     * 앨범 이름으로 간단히 검색합니다 (Spring Data Repository 메서드 사용).
     */
    public List<AlbumDocument> searchAlbumsByName(String name) {
        return albumDocumentRepository.findByNameContaining(name);
    }

    /**
     * [1. 자동 완성] 입력된 접두사(Prefix)를 이용하여 앨범 이름의 추천 목록을 제공합니다.
     * Edge N-gram이 적용된 name 필드를 대상으로 Match Query를 사용합니다.
     */
    public List<String> searchAutocompleteSuggestions(String prefix, int size) {
        // 앨범 이름이 한국어 분석기(autocomplete_analyzer)로 인덱싱되었다는 가정 하에 쿼리
        var searchQuery = new NativeQueryBuilder()
                .withQuery(q -> q
                        // Match Query를 사용하여 Edge N-gram 토큰과 일치하는 결과를 찾음
                        .match(m -> m
                                .field("name")
                                .query(prefix)
                                // AND 연산자는 정확도를 높임 (필요에 따라 제거 가능)
                                .operator(Operator.And)
                        )
                )
                .withMaxResults(size) // 결과 수 제한
                .build();

        var searchHits = elasticsearchOperations.search(searchQuery, AlbumDocument.class);

        // 앨범 이름만 추출하고 중복을 제거하여 반환
        return searchHits.stream()
                .map(SearchHit::getContent)
                .map(AlbumDocument::getName)
                .distinct()
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------

    /**
     * [2. 전체 검색] 이름 또는 프로필 필드에서 키워드 검색을 수행합니다.
     * 여러 필드에서 관련성이 높은 결과를 찾기 위해 CriteriaQuery를 사용합니다.
     */
    public List<AlbumDocument> searchAlbumsByKeyword(String keyword) {
        // 이름 또는 프로필 필드에서 키워드 포함 검색 조건 설정
        Criteria criteria = new Criteria("name").contains(keyword)
                .or(new Criteria("profile").contains(keyword));

        // 쿼리 생성
        CriteriaQuery query = new CriteriaQuery(criteria);

        // 쿼리 실행
        var searchHits = elasticsearchOperations.search(query, AlbumDocument.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
