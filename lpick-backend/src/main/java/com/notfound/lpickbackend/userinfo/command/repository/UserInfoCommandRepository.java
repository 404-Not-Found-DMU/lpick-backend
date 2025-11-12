package com.notfound.lpickbackend.userinfo.command.repository;

import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoCommandRepository extends JpaRepository<UserInfo, String> {

    @EntityGraph(attributePaths = {"userAuthList", "userAuthList.auth"})
    Optional<UserInfo> findByOauthId(String oauthId);

    @Modifying
    @Query("""
        UPDATE UserInfo u SET
          u.point = u.point + :plusPoint,
          u.stackPoint = u.stackPoint + CASE WHEN :plusPoint > 0 THEN :plusPoint ELSE 0 END
        WHERE u.oauthId = :userId
    """)
    int addPoint(String userId, int plusPoint);

}
