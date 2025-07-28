package com.notfound.lpickbackend.userinfo.query.repository;

import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserGear;
import com.notfound.lpickbackend.userinfo.query.dto.response.usergear.GearInfoResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGearQueryRepository extends JpaRepository<UserGear, String> {

    @Query("""
        SELECT DISTINCT new com.notfound.lpickbackend.userinfo.query.dto.response.usergear.GearInfoResponse(
            ug.userGearId,
            g.name,
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
}
