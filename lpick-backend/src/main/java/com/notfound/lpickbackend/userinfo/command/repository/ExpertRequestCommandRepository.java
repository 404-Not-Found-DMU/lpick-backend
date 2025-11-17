package com.notfound.lpickbackend.userinfo.command.repository;


import com.notfound.lpickbackend.userinfo.command.application.domain.entity.ExpertRequest;
import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.ExpertRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ExpertRequestCommandRepository extends JpaRepository<ExpertRequest, String> {

    // 원하는 status를 기입하여 해당 조건에 맞는 값 확인 가능
    boolean existsByUserInfo_OauthIdAndStatusIn(
            String oauthId,
            Collection<ExpertRequestStatus> statuses
    );

    Optional<ExpertRequest> findByUserInfo_OauthIdAndStatusIn(
            String oauthId,
            Collection<ExpertRequestStatus> statuses
    );
}
