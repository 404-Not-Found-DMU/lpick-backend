package com.notfound.lpickbackend.userinfo.query.repository;

import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserAlbum;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserAlbumOwnedHeader;
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
    @Query(
            value = """
  select new com.notfound.lpickbackend.userinfo.query.dto.response.UserAlbumOwnedHeader(
    ua.userAlbumId,
    al.name,
    al.profile,
    case
      when count(distinct ar.artistId) > 1 then
        concat(
          min(ar.name),
          concat(' + 외 ', concat( (count(distinct ar.artistId) - 1), '개 아티스트'))
        )
      when count(distinct ar.artistId) = 1 then
        min(ar.name)
      else
        '(아티스트 미상)'
    end,
    ua.isFavorite
  )
  from UserAlbum ua
    join ua.album al
    left join ArtistAlbum aral on aral.album = al
    left join aral.artist ar
  where ua.oauth.oauthId = :oauthId
  group by
    ua.userAlbumId,
    al.name, al.profile,
    ua.isFavorite
  """,
            countQuery = """
  select count(distinct ua.userAlbumId)
  from UserAlbum ua
  where ua.oauth.oauthId = :oauthId
  """
    )
    public Page<UserAlbumOwnedHeader> findAllUserAlbumByOauthId(String oauthId, Pageable pageable);

    // 아티스트가 여러명인경우 , 로 묶어 하나의 string으로 전달. artist 없으면 아티스트 미상으로 전달
    @Query("""
select new com.notfound.lpickbackend.userinfo.query.dto.response.UserAlbumOwnedResponse(
  ua.userAlbumId,
  al.name,
  al.profile,
  ua.createdAt,
  coalesce( cast(function('string_agg', ar.name, ', ') as string), '(아티스트 미상)' ),
  ua.recordFile,
  al.releaseDate,
  al.releaseCountry,
  al.label,
  ua.isFavorite
)
from UserAlbum ua
  join ua.album al
  left join ArtistAlbum aral on aral.album = al
  left join aral.artist ar
where ua.userAlbumId = :userAlbumId
group by
  ua.userAlbumId, al.name, al.profile, ua.createdAt,
  ua.recordFile, al.releaseDate, al.releaseCountry, al.label, ua.isFavorite
""")
    Optional<UserAlbumOwnedResponse> findUserAlbumById(String userAlbumId);

    @Query("""
select new com.notfound.lpickbackend.userinfo.query.dto.response.UserAlbumOwnedHeader(
  ua.userAlbumId,
  al.name,
  al.profile,
  case
    when count(distinct ar.artistId) > 1 then
      concat(min(ar.name), concat(' + 외 ', concat((count(distinct ar.artistId) - 1), '개 아티스트')))
    when count(distinct ar.artistId) = 1 then
      min(ar.name)
    else
      '(아티스트 미상)'
  end,
  ua.isFavorite
)
from UserAlbum ua
  join ua.album al
  left join ArtistAlbum aral on aral.album = al
  left join aral.artist ar
where ua.oauth.oauthId = :oauthId
  and ua.isFavorite = true
group by
  ua.userAlbumId,
  al.name, al.profile,
  ua.isFavorite
order by ua.createdAt desc, ua.userAlbumId desc
""")
    List<UserAlbumOwnedHeader> findAllUserAlbumByIsFavorite(String oauthId);

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
