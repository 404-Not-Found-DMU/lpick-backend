package com.notfound.lpickbackend.support.query.controller;
import com.notfound.lpickbackend.support.query.dto.QuestionAndAnswerDetailResponse;
import com.notfound.lpickbackend.support.query.dto.QuestionAndAnswerListResponse;
import com.notfound.lpickbackend.support.query.service.QuestionQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/question")
@Tag(name = "문의사항 조회 컨트롤러", description = "문의사항 목록을 조회하는 컨트롤러입니다.")
public class QuestionQueryController {

     private final QuestionQueryService questionQueryService;

    @GetMapping
    @Operation(summary = "문의사항 검색", description = "문의사항 검색 api입니다. 기본적인 전체조회는 keyword 없이 보내시면 됩니다.")
    public ResponseEntity<Page<QuestionAndAnswerListResponse>> searchQuestionAndAnswer(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return ResponseEntity.ok(questionQueryService.searchQuestion(keyword, pageable));
    }

    @GetMapping("/{questionId}")
    @Operation(summary = "문의사항 상세조회", description = "questionId로 문의사항 상세내역을 조회합니다. answer도 존재하면 함께 조회합니다.")
    public ResponseEntity<QuestionAndAnswerDetailResponse> readQuestionDetail(
            @PathVariable(name = "questionId") String questionId
    ) {

        return ResponseEntity.ok(questionQueryService.readQuestionDetail(questionId));
    }
}
