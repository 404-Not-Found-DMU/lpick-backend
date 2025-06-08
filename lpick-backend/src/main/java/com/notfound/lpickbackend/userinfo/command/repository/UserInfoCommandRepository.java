package com.notfound.lpickbackend.userinfo.command.repository;

import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoCommandRepository extends JpaRepository<UserInfo, String> {

    @EntityGraph(attributePaths = {"userAuthList", "userAuthList.auth"})
    Optional<UserInfo> findByOauthId(String oauthId);

}
