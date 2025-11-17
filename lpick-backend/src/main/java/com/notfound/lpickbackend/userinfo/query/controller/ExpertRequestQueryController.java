package com.notfound.lpickbackend.userinfo.query.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.ExpertRequestStatus;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.ExpertAdvancementRequest;
import com.notfound.lpickbackend.userinfo.query.dto.response.ExpertAdvancementAdminDetailResponse;
import com.notfound.lpickbackend.userinfo.query.dto.response.ExpertAdvancementAdminResponse;
import com.notfound.lpickbackend.userinfo.query.dto.response.ExpertAdvancementUserResponse;
import com.notfound.lpickbackend.userinfo.query.service.ExpertRequestQueryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ExpertRequestQueryController {

    private final ExpertRequestQueryService expertRequestQueryService;


    @GetMapping("/expert-request")
    @Operation(summary = "전문가 등업 신청 상태 확인", description = "[사용자] 본인의 전문가 등업 신청 상태 확인. 신청후 대기중이거나, 반려된 인원만 확인 가능합니다. 가장 마지막에 신청했던 등업 신청 내역만 전달합니다.")
    public ResponseEntity<ExpertAdvancementUserResponse> getExpertRequest(
            @AuthenticationPrincipal OAuth2UserDetails userdetail
    ) {
        return ResponseEntity.ok(expertRequestQueryService.findOwnedExpertRequestList(userdetail.getUsername()));
    }


    @GetMapping("/expert-request-list")
    @Operation(summary = "전문가 등업 신청 내역 확인", description = "[관리자] 사용자들이 보낸 전문가 등업 신청 목록을 최신순으로 확인. 승낙, 반려가 결정되지 않은 내역만 확인할 수 있습니다.")
    public ResponseEntity<Page<ExpertAdvancementAdminResponse>> getExpertRequestPageable(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) ExpertRequestStatus status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);


        return ResponseEntity.ok(expertRequestQueryService.getExpertRequestSearchPagenation(keyword, status, pageable));
    }

    @GetMapping("/expert-request/{expertRequestId}")
    @Operation(summary = "전문가 등업 신청 상세 내역 확인", description = "[관리자] 사용자들이 보낸 전문가 등업 신청 하나의 상세 내역을 확인합니다.")
    public ResponseEntity<ExpertAdvancementAdminDetailResponse> getExpertRequestDetail(
            @PathVariable("expertRequestId") String expertRequestId
    ) {
        return ResponseEntity.ok(expertRequestQueryService.getExpertRequestDetail(expertRequestId));
    }
}
