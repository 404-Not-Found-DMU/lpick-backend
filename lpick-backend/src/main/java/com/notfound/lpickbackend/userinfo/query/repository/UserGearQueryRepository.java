package com.notfound.lpickbackend.userinfo.query.repository;

import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserGear;
import com.notfound.lpickbackend.userinfo.query.dto.response.usergear.GearInfoResponse;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGearQueryRepository extends JpaRepository<UserGear, String> {
    // GearCollection == 사용자에게 반환하기위한 목적의 내역 반환
    // GearDetail == 서버 내에서 각 기능에 활용하기위한 목적의 N+1 방지 목적 EntityGraph 적용 엔티티 반환


    @Query("""
        SELECT DISTINCT new com.notfound.lpickbackend.userinfo.query.dto.response.usergear.GearInfoResponse(
            ug.userGearId,
            g.name,
            ug.isFavorite,
            g.modelName,
            g.brand,
            eq.className,
            g.wiki.wikiId
        )
        FROM UserGear ug
        JOIN ug.eq g
        JOIN g.eqClass eq
        WHERE ug.userGearId = :gearId
    """)
    Optional<GearInfoResponse> findGearCollectionById(
            @Param("gearId") String userGearId
    );

    @Query("""
        SELECT DISTINCT new com.notfound.lpickbackend.userinfo.query.dto.response.usergear.GearInfoResponse(
            ug.userGearId,
            g.name,
            ug.isFavorite,
            g.modelName,
            g.brand,
            eq.className,
            g.wiki.wikiId
        )
        FROM UserGear ug
        JOIN ug.eq g
        JOIN g.eqClass eq
        WHERE ug.oauth.oauthId = :oauthId
    """)
    List<GearInfoResponse> findAllGearCollectionByUserId(
            @Param("oauthId") String oauthId
    );

    // 메소드명 명시적 작성 위해 쿼리구문으로 작성.
    /** EntityGraph 대상 : eq, eq.class || 음향기기 및 음향기기 클래스 명칭까지 한번에 확인 필요한(Detail) 엔티티 필요시 사용 요망.*/
    @Query("""
      SELECT ug
      FROM   UserGear ug
        JOIN FETCH ug.eq g
        JOIN FETCH g.eqClass c
      WHERE  ug.userGearId = :userGearId
    """)
    @EntityGraph(attributePaths = {"eq", "eq.eqClass"})
    Optional<UserGear> findGearDetailByIdWithEqAndEqClass(String userGearId);

    /**
     * 주어진 oauthId 사용자가 즐겨찾기(isFavorite=true)한 기어 중,
     * eq.eqClass.className이 파라미터와 동일한 것의 개수를 반환
     */
    @Query("""
      SELECT COUNT(ug)
      FROM   UserGear ug
      JOIN   ug.eq g
      JOIN   g.eqClass eq
      WHERE  eq.className = :className
        AND  ug.isFavorite = TRUE
        AND  ug.oauth.oauthId = :oauthId
    """)
    long countFavoritesByOauthIdAndClassName(
            @Param("oauthId")   String oauthId,
            @Param("className") String className
    );
}
