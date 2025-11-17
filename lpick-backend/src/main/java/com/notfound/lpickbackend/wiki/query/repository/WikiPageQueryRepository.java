package com.notfound.lpickbackend.wiki.query.repository;

import com.notfound.lpickbackend.servicedata.query.dto.SearchResultWithImage;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WikiPageQueryRepository extends JpaRepository<WikiPage, String> {

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

    @Query(value = "SELECT * FROM wiki_page TABLESAMPLE SYSTEM (0.001) LIMIT 1",
            nativeQuery = true)
    WikiPage findRandomOne();
}
