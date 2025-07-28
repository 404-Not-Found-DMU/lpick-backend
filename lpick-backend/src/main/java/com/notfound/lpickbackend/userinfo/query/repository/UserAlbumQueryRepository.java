package com.notfound.lpickbackend.userinfo.query.repository;

import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserAlbum;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserAlbumOwnedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAlbumQueryRepository extends JpaRepository<UserAlbum, String> {

    // JPQL로 뽑아내는 테이블들을 추후 View 테이블을 사용해보는 걸 논의해보는 것도 좋을 듯 합니다.
    /** 사용자가 소유중인 앨범의 정보(앨범명, 앨범사진, 아티스트명, 녹음파일, 발매일, 발매국가, 레이블) 제공 */
    @Query("""
    SELECT new com.notfound.lpickbackend.userinfo.query.dto.response.UserAlbumOwnedResponse(
        ua.userAlbumId,
        al.name,
        al.profile,
        ar.name,
        ua.recordFile,
        al.releaseDate,
        al.releaseCountry,
        al.label,
        ua.isFavorite
    )
    FROM UserAlbum ua
    LEFT JOIN Album al ON al.albumId = ua.album.albumId
    LEFT JOIN ArtistAlbum aral ON aral.album.albumId = ua.album.albumId
    LEFT JOIN Artist ar ON ar.artistId = aral.artist.artistId
    WHERE ua.oauth.oauthId = :oauthId
    GROUP BY al.albumId, aral.album.albumId, ar.artistId
    """)
    public Page<UserAlbumOwnedResponse> findAllUserAlbumByOauthId(String oauthId, Pageable pageable);

    @Query("""
    SELECT new com.notfound.lpickbackend.userinfo.query.dto.response.UserAlbumOwnedResponse(
        ua.userAlbumId,
        al.name,
        al.profile,
        ar.name,
        ua.recordFile,
        al.releaseDate,
        al.releaseCountry,
        al.label,
        ua.isFavorite
    )
    FROM UserAlbum ua
    LEFT JOIN Album al ON al.albumId = ua.album.albumId
    LEFT JOIN ArtistAlbum aral ON aral.album.albumId = ua.album.albumId
    LEFT JOIN Artist ar ON ar.artistId = aral.artist.artistId
    WHERE ua.userAlbumId = :userAlbumId
    GROUP BY al.albumId, aral.album.albumId, ar.artistId
    """)
    Optional<UserAlbumOwnedResponse> findUserAlbumById(String userAlbumId);

    @Query("""
    SELECT new com.notfound.lpickbackend.userinfo.query.dto.response.UserAlbumOwnedResponse(
        ua.userAlbumId,
        al.name,
        al.profile,
        ar.name,
        ua.recordFile,
        al.releaseDate,
        al.releaseCountry,
        al.label,
        ua.isFavorite
    )
    FROM UserAlbum ua
    LEFT JOIN Album al ON al.albumId = ua.album.albumId
    LEFT JOIN ArtistAlbum aral ON aral.album.albumId = ua.album.albumId
    LEFT JOIN Artist ar ON ar.artistId = aral.artist.artistId
    WHERE ua.oauth.oauthId = :oauthId AND ua.isFavorite = TRUE
    GROUP BY al.albumId, aral.album.albumId, ar.artistId
    """)
    List<UserAlbumOwnedResponse> findAllUserAlbumByIsFavorite(String oauthId);

    /**
     * 장르별 보유 앨범 수를 (장르명, 개수) 쌍의 List<Object[]> 로 리턴
     * Object[0] = genre (String)
     * Object[1] = count (Long)
     */
    @Query("""
        SELECT ag.genre.name, COUNT(ua)
        FROM UserAlbum ua
        LEFT JOIN Album al ON ua.album.albumId = al.albumId
        LEFT JOIN AlbumGenre ag ON ua.album.albumId = ag.album.albumId
        WHERE ua.oauth.oauthId = :oAuthId
        GROUP BY ag.genre.name
        ORDER BY COUNT(ua) DESC
    """)
    List<Object[]> countUserAlbumByGenre(String oAuthId); // [ ["Rock", 5], ["Jazz", 3], ...] 같은 방식으로 반환

    long countByOauth_OauthIdAndIsFavoriteTrue(String oauthId);
}
