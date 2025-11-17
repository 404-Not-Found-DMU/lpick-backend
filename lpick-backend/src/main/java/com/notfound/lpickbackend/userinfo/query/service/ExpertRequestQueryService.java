package com.notfound.lpickbackend.userinfo.query.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.common.s3.dto.S3DataValue;
import com.notfound.lpickbackend.common.s3.util.FileInfoResolver;
import com.notfound.lpickbackend.servicedata.query.service.UnifiedSearchService;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.ExpertRequest;
import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.ExpertRequestStatus;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.ExpertAdvancementRequest;
import com.notfound.lpickbackend.userinfo.query.dto.response.ExpertAdvancementAdminDetailResponse;
import com.notfound.lpickbackend.userinfo.query.dto.response.ExpertAdvancementAdminResponse;
import com.notfound.lpickbackend.userinfo.query.dto.response.ExpertAdvancementUserResponse;
import com.notfound.lpickbackend.userinfo.query.repository.ExpertRequestQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpertRequestQueryService {
    private final ExpertRequestQueryRepository expertRequestQueryRepository;

    private final UnifiedSearchService unifiedSearchService;

    /** 사용자 본인의 ExpertRequest 상태 확인 */
    public ExpertAdvancementUserResponse findOwnedExpertRequestList(String oauthId) {
        ExpertRequest targetRequest = expertRequestQueryRepository
                .findTop1ByUserInfo_OauthIdOrderByCreatedAtDesc(oauthId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_EXPERT_REQUEST));

        return ExpertAdvancementUserResponse.builder()
                .status(targetRequest.getStatus())
                .requestMemo(targetRequest.getRequestMemo())
                .rejectedCause(targetRequest.getRejectedCause())
                .createdAt(targetRequest.getCreatedAt())
                .modifiedAt(targetRequest.getDecisionAt())
                .build();
    }

    /** 관리자 페이지에서 ExpertRequest 확인 */
    public Page<ExpertAdvancementAdminResponse> getExpertRequestSearchPagenation(String keyword, ExpertRequestStatus status, Pageable pageable) {
        return unifiedSearchService.searchExpertRequests(keyword, pageable, status);
    }


    public ExpertAdvancementAdminDetailResponse getExpertRequestDetail(String expertRequestId) {
        ExpertRequest expertRequest = expertRequestQueryRepository.findExpertRequestDetailByExpertRequestId(expertRequestId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_EXPERT_REQUEST));


        return ExpertAdvancementAdminDetailResponse.from(expertRequest);
    }
}
