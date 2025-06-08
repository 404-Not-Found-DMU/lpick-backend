package com.notfound.lpickbackend.userinfo.query.repository;

import com.notfound.lpickbackend.userinfo.command.application.domain.UserAuth;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserAuthId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthQueryRepository extends JpaRepository<UserAuth, UserAuthId> {
    Optional<UserAuth> findByOauth_OauthId(String oauthId);
}
