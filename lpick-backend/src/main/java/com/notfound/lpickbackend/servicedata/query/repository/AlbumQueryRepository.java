package com.notfound.lpickbackend.servicedata.query.repository;

import com.notfound.lpickbackend.common.elasticsearch.repository.KeysetPageRepository;
import com.notfound.lpickbackend.servicedata.command.application.domain.Album;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumQueryRepository extends JpaRepository<Album, String>, KeysetPageRepository<Album> {

    /**
     * 특정 LPTI를 가진 앨범 중 random_point를 기준으로 5개의 앨범을 랜덤 조회합니다.
     * 이 쿼리는 두 부분으로 나뉘어 실행됩니다:
     * 1. 랜덤 기준점(randomPoint)보다 크거나 같은 앨범을 5개 찾습니다.
     * 2. (결과가 5개 미만일 경우) 나머지 부족한 앨범을 찾습니다.
     *
     * @param lpti 조회할 LPTI 코드
     * @param randomPoint 랜덤 기준점 (0.0 ~ 1.0)
     * @return 랜덤 앨범 5개 리스트
     */
    @Query(value = """
        (
            SELECT a.*
            FROM album a
            WHERE a.lpti = :lpti AND a.random_point >= :randomPoint
            ORDER BY a.random_point
            LIMIT 5
        )
        UNION ALL
        (
            SELECT a.*
            FROM album a
            WHERE a.lpti = :lpti AND a.random_point < :randomPoint
            ORDER BY a.random_point DESC
            LIMIT :limit - (
                SELECT COUNT(*) FROM album 
                WHERE lpti = :lpti AND random_point >= :randomPoint
            )
        )
        LIMIT 5
    """, nativeQuery = true)
    List<Album> findRandom5ByLpti(@Param("lpti") String lpti,
                                  @Param("randomPoint") double randomPoint,
                                  @Param("limit") int limit);

    List<Album> findTop5ByReleaseDateIsNotNullOrderByReleaseDateDesc();

    @Override
    @Query("""
        SELECT a
        FROM Album a
        WHERE (:lastId IS NULL OR a.albumId > :lastId)
        ORDER BY a.albumId ASC
        """)
    List<Album> findNextPage(@Param("lastId") String lastId, Pageable pageable);
}
