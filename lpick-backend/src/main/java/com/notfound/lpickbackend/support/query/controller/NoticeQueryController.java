package com.notfound.lpickbackend.support.query.controller;

import com.notfound.lpickbackend.support.query.dto.NoticeDetailResponse;
import com.notfound.lpickbackend.support.query.dto.NoticeListResponse;
import com.notfound.lpickbackend.support.query.dto.QuestionAndAnswerListResponse;
import com.notfound.lpickbackend.support.query.service.NoticeQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notice")
@Tag(name = "공지사항 조회 컨트롤러", description = "공지사항 검색 컨트롤러입니다.")
public class NoticeQueryController {

     private final NoticeQueryService noticeQueryService;

    @GetMapping
    @Operation(summary = "공지사항 검색", description = "공지사항 검색 api입니다. 기본적인 전체조회는 keyword 없이 보내시면 됩니다.")
    public ResponseEntity<Page<NoticeListResponse>> searchQuestionAndAnswer(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return ResponseEntity.ok(noticeQueryService.searchNotice(keyword, pageable));
    }

    @GetMapping("/{noticeId}")
    @Operation(summary = "공지사항 상세 조회", description = "noticeId로 공지사항 상세내역을 조회합니다.")
    public ResponseEntity<NoticeDetailResponse> getNoticeById(
            @PathVariable("noticeId") String noticeId
    ) {

        return ResponseEntity.ok(noticeQueryService.readNoticeDetail(noticeId));
    }
}
