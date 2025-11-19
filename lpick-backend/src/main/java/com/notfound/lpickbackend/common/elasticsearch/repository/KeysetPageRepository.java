package com.notfound.lpickbackend.common.elasticsearch.repository;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface KeysetPageRepository<T> {

    /**
     * lastId 이후의 데이터를 ID 오름차순으로 조회하는 공통 인터페이스.
     * 실제 @Query 는 각 엔티티별 Repository 에서 작성.
     */
    List<T> findNextPage(String lastId, Pageable pageable);
}