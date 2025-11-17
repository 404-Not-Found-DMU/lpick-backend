package com.notfound.lpickbackend.userinfo.query.repository;

import com.notfound.lpickbackend.userinfo.command.application.domain.entity.ExpertRequest;
import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.ExpertRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ExpertRequestQueryRepository extends JpaRepository<ExpertRequest, String> {
    // 상태와 관계없이, 특정 유저의 가장 마지막 신청 하나
    Optional<ExpertRequest> findTop1ByUserInfo_OauthIdOrderByCreatedAtDesc(String oauthId);

    Page<ExpertRequest> findAllByStatusIn(
            Collection<ExpertRequestStatus> statuses,
            Pageable pageable
    );


    @EntityGraph(attributePaths = {"userInfo"})
    Optional<ExpertRequest> findExpertRequestDetailByExpertRequestId(String expertRequestId);
}
