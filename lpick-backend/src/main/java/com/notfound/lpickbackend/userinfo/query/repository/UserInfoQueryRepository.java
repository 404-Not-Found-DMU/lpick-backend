package com.notfound.lpickbackend.userinfo.query.repository;

import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserInfoResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoQueryRepository extends JpaRepository<UserInfo, String> {

    @Query("""
        select new com.notfound.lpickbackend.userinfo.query.dto.response.UserInfoResponse(
            u.oauthId,
            u.nickname,
            u.about,
            u.profile,
            u.lpti
        )
        from UserInfo u
        where u.oauthId = :userId
    """)
    UserInfoResponse findByOAuthId(@Param("userId") String userId);
}
