package com.notfound.lpickbackend.support.command.application.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.support.command.application.dto.AnswerRequest;
import com.notfound.lpickbackend.support.command.application.service.AnswerCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "문의사항 답변 관리 컨트롤러", description = "문의사항 답변 생성/수정/삭제 컨트롤러")
public class AnswerCommandController {

     private final AnswerCommandService answerCommandService;

    @PostMapping("/question/{questionId}")
    @Operation(summary = "답변 생성", description = "답변 생성 API입니다.")
    public ResponseEntity<SuccessCode> createAnswer(
            @RequestBody AnswerRequest answerRequest,
            @PathVariable(name = "questionId") String questionId
    ) {

        answerCommandService.createAnswer(answerRequest, questionId);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }

    @PatchMapping("/answer/{answerId}")
    @Operation(summary = "답변 수정", description = "답변 수정 API입니다.")
    public ResponseEntity<SuccessCode> updateAnswer(
            @RequestBody AnswerRequest answerRequest,
            @PathVariable(name = "answerId") String answerId
    ) {
        answerCommandService.updateAnswer(answerRequest, answerId);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }

    @DeleteMapping("/answer/{answerId}")
    @Operation(summary = "답변 삭제", description = "답변 삭제 API입니다.")
    public ResponseEntity<SuccessCode> deleteAnswer(
            @PathVariable(name = "answerId") String answerId
    ) {
        answerCommandService.deleteAnswer(answerId);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }

}
