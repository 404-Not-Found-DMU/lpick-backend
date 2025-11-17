package com.notfound.lpickbackend.userinfo.query.repository;

import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserGear;
import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.GearClass;
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


//    @Query("""
//        SELECT DISTINCT new com.notfound.lpickbackend.userinfo.query.dto.response.usergear.GearInfoResponse(
//            ug.userGearId,
//            g.name,
//            ug.isFavorite,
//            g.modelName,
//            g.brand,
//            eq.className,
//            g.wiki.wikiId
//        )
//        FROM UserGear ug
//        JOIN ug.eq g
//        JOIN g.eqClass eq
//        WHERE ug.userGearId = :gearId
//    """)
//    Optional<GearInfoResponse> findGearCollectionById(
//            @Param("gearId") String userGearId
//    );

//    @Query("""
//        SELECT DISTINCT new com.notfound.lpickbackend.userinfo.query.dto.response.usergear.GearInfoResponse(
//            ug.userGearId,
//            g.name,
//            ug.isFavorite,
//            g.modelName,
//            g.brand,
//            eq.className,
//            g.wiki.wikiId
//        )
//        FROM UserGear ug
//        JOIN ug.eq g
//        JOIN g.eqClass eq
//        WHERE ug.oauth.oauthId = :oauthId
//    """)
//    List<GearInfoResponse> findAllGearCollectionByUserId(
//            @Param("oauthId") String oauthId
//    );

    /** UserGear를 eqClass 각 분류 별로 1개씩 가져온다.
     * isFavorite이 있으면 해당 내역을 0순위로 추출.
     * isFavoirte 내역이 없는 경우, createdAt이 가장 늦은 것을 추출.*/
    @Query(""" 
        SELECT new com.notfound.lpickbackend.userinfo.query.dto.response.usergear.GearInfoResponse( 
            ug.userGearId, 
            g.name, 
            ug.isFavorite, 
            g.modelName, 
            g.brand, 
            eq.className, 
            g.img,
            g.wiki.wikiId 
            ) 
        FROM UserGear ug 
        JOIN ug.eq g 
        JOIN g.eqClass eq 
        WHERE ug.oauth.oauthId = :oauthId AND eq.className IN ('TURNTABLE', 'SPEAKER', 'HEADPHONE') 
            AND ug.id IN ( 
                SELECT ug2.id 
                FROM UserGear ug2 
                JOIN ug2.eq g2 
                JOIN g2.eqClass eq2 
                WHERE ug2.oauth.oauthId = :oauthId AND eq2.className = eq.className 
                ORDER BY CASE WHEN ug2.isFavorite = TRUE THEN 1 ELSE 0 END DESC, 
                ug2.createdAt DESC LIMIT 1 
        ) 
    """)
    List<GearInfoResponse> findAllGearCollectionByUserId_V1(
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
    
    /** gearId와 oauthId로 UserGear를 특정해 전달 */
    Optional<UserGear> findByUserGearIdAndOauth_OauthId(String gearId, String oauthId);

    /**
     * 정렬 기준
     * 1순위: isFavorite = true
     * 2순위: createdAt DESC (최신순)
     * */
    @Query("""
    SELECT new com.notfound.lpickbackend.userinfo.query.dto.response.usergear.GearInfoResponse(
        ug.userGearId,
        g.name,
        ug.isFavorite,
        g.modelName,
        g.brand,
        eq.className,
        g.img,
        g.wiki.wikiId
    )
    FROM UserGear ug
    JOIN ug.eq g
    JOIN g.eqClass eq
    WHERE ug.oauth.oauthId = :oauthId
      AND eq = :gearClass
    ORDER BY 
        CASE WHEN ug.isFavorite = TRUE THEN 1 ELSE 0 END DESC,
        ug.createdAt DESC
""")
    List<GearInfoResponse> findAllUserGearListByUserIdAndGearClass_V1(
            @Param("oauthId") String oauthId,
            @Param("gearClass") String gearClass
    );
}
