package com.notfound.lpickbackend.support.command.application.controller;

import com.notfound.lpickbackend.common.dto.IdResponse;
import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.support.command.application.dto.NoticeRequest;
import com.notfound.lpickbackend.support.command.application.service.NoticeCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notice")
@Tag(name = "공지사항 관리 컨트롤러", description = "공지사항 생성/수정/삭제 컨트롤러")
public class NoticeCommandController {

     private final NoticeCommandService noticeCommandService;

    @PostMapping
    @Operation(summary = "공지사항 생성", description = "공지사항 생성 API입니다.")
    public ResponseEntity<IdResponse> createNotice(
            @RequestBody NoticeRequest noticeRequest
    ) {

        String id = noticeCommandService.createNotice(noticeRequest);

        return ResponseEntity.ok(new IdResponse(id));
    }

    @PatchMapping("/{noticeId}")
    @Operation(summary = "공지사항 수정", description = "공지사항 수정 API입니다.")
    public ResponseEntity<SuccessCode> updateNotice(
            @RequestBody NoticeRequest noticeRequest,
            @PathVariable(name = "noticeId") String noticeId
    ) {

        noticeCommandService.updateNotice(noticeRequest, noticeId);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }

    @DeleteMapping("/{noticeId}")
    @Operation(summary = "공지사항 삭제", description = "공지사항 삭제 API입니다.")
    public ResponseEntity<SuccessCode> deleteNotice(
            @PathVariable(name = "noticeId") String noticeId
    ) {

        noticeCommandService.deleteNotice(noticeId);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }
}
