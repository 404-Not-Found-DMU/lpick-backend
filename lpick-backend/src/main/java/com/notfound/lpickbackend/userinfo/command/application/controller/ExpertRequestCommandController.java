package com.notfound.lpickbackend.userinfo.command.application.controller;


import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.ExpertAdvancementRequest;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.ExpertRequestRejectCause;
import com.notfound.lpickbackend.userinfo.command.application.service.ExpertRequestCommandService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ExpertRequestCommandController {

    private final ExpertRequestCommandService expertRequestCommandService;


    /** 전문가 등업신청 */
    @PostMapping(
            value = "/expert-request",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "전문가 등업 신청", description = "[사용자] 전문가 등업 신청. 최소한 1개 이상의 멀티파트 파일을 반드시 포함해야합니다. 이미 신청중이거나 승급이 완료된 회원은 사용할 수 없습니다.")
    public ResponseEntity<SuccessCode> createExpertRequest(
            @RequestPart("payload") @Valid ExpertAdvancementRequest request,
            @RequestPart(value = "files", required = true) List<MultipartFile> files,
            @AuthenticationPrincipal OAuth2UserDetails userdetail
    ) {
        expertRequestCommandService.expertAdvancementRequest(userdetail.getUsername(), request, files);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }

    /** 전문가 등업신청 */
    @PatchMapping(
            value = "/expert-request/{expertRequestId}/accept")
    @Operation(summary = "전문가 등업 신청 승낙", description = "[관리자] 대상 등업신청 승낙.")
    public ResponseEntity<SuccessCode> acceptExpertRequest(
            @PathVariable("expertRequestId") String requestId
    ) {
        expertRequestCommandService.acceptExpertRequest(requestId);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }

    /** 전문가 등업신청 */
    @PatchMapping(
            value = "/expert-request/{expertRequestId}/reject")
    @Operation(summary = "전문가 등업 신청", description = "[관리자] 대상 등업신청 반려.")
    public ResponseEntity<SuccessCode> rejectExpertRequest(
            @PathVariable("expertRequestId") String requestId,
            @RequestBody ExpertRequestRejectCause cause
    ) {
        expertRequestCommandService.rejectExpertRequest(cause, requestId);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }

    /** 전문가 등업 신청 삭제 */
    @DeleteMapping(
            value = "/expert-request")
    @Operation(summary = "전문가 등업 신청 취소", description = "[사용자] 사용자 본인이 신청했던 내역을 제거 가능합니다(아직 승인/반려되지 않은 경우에 한정). 삭제 시 S3에 업로드된 파일 또한 전부 삭제됩니다.")
    public ResponseEntity<SuccessCode> createExpertRequest(
            @AuthenticationPrincipal OAuth2UserDetails userdetail) {
        expertRequestCommandService.expertAdvancementDelete(userdetail.getUsername());

        return ResponseEntity.ok(SuccessCode.NO_CONTENT);
    }


}
