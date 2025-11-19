package com.notfound.lpickbackend.wiki.query.repository;

import com.notfound.lpickbackend.common.elasticsearch.repository.KeysetPageRepository;
import com.notfound.lpickbackend.servicedata.command.application.domain.Album;
import com.notfound.lpickbackend.servicedata.query.dto.SearchResultWithImage;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WikiPageQueryRepository extends JpaRepository<WikiPage, String>, KeysetPageRepository<WikiPage> {

    @Query("SELECT wp.wikiId FROM WikiPage wp WHERE wp.artist.artistId = :artistId")
    Optional<String> findWikiIdByArtistId(@Param("artistId") String artistId);

    /**
     * Album의 albumId를 기준으로 WikiPage의 wikiId를 조회합니다.
     * (Album 엔티티에 albumId 필드가 있다고 가정)
     * @param albumId 앨범 ID
     * @return Optional<String> wikiId
     */
    @Query("SELECT wp.wikiId FROM WikiPage wp WHERE wp.album.albumId = :albumId")
    Optional<String> findWikiIdByAlbumId(@Param("albumId") String albumId);

    /**
     * Gear의 gearId를 기준으로 WikiPage의 wikiId를 조회합니다.
     * (Gear 엔티티에 gearId 필드가 있다고 가정)
     * @param gearId 장비 ID
     * @return Optional<String> wikiId
     */
    @Query("SELECT wp.wikiId FROM WikiPage wp WHERE wp.gear.id = :gearId")
    Optional<String> findWikiIdByGearId(@Param("gearId") String gearId);

    @EntityGraph(attributePaths = {"album"})
    @Query("SELECT wp FROM WikiPage wp WHERE wp.album.albumId IN :albumIds")
    List<WikiPage> findWikiByAlbumIdsIn(@Param("albumIds") List<String> albumIds);

    // 1. 랜덤값보다 크거나 같은 첫 번째 녀석 (전체 스캔 X, 인덱스 스캔 O)
    Optional<WikiPage> findFirstByRandomPointGreaterThanEqualOrderByRandomPointAsc(Double randomPoint);

    // 2. 범위 밖일 경우를 대비해 맨 처음 녀석 조회 (Fallback)
    Optional<WikiPage> findFirstByOrderByRandomPointAsc();

    @Override
    @Query("""
    SELECT w
    FROM WikiPage w
        LEFT JOIN FETCH w.artist
        LEFT JOIN FETCH w.album
        LEFT JOIN FETCH w.gear
    WHERE (:lastId IS NULL OR w.wikiId > :lastId)
    ORDER BY w.wikiId ASC
    """)
    List<WikiPage> findNextPage(@Param("lastId") String lastId, Pageable pageable);
}
