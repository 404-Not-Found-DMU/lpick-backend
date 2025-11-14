package com.notfound.lpickbackend.support.command.application.controller;
import com.notfound.lpickbackend.common.dto.IdResponse;
import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.support.command.application.dto.QuestionRequest;
import com.notfound.lpickbackend.support.command.application.service.QuestionCommandService;
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
@RequestMapping("/api/v1/question")
@Tag(name = "문의사항 답변 컨트롤러", description = "문의사항 답변 생성/수정/삭제 컨트롤러")
public class QuestionCommandController {

     private final QuestionCommandService questionCommandService;

    @PostMapping
    @Operation(summary = "문의사항 생성", description = "문의사항 생성 API입니다.")
    public ResponseEntity<IdResponse> createQuestion(
            @RequestBody QuestionRequest questionRequest
            ) {

        String id = questionCommandService.createQuestion(questionRequest);

        return ResponseEntity.ok(new IdResponse(id));
    }

    @PatchMapping("/{questionId}")
    @Operation(summary = "문의사항 수정", description = "문의사항 수정 API입니다.")
    public ResponseEntity<SuccessCode> updateQuestion(
            @RequestBody QuestionRequest questionRequest,
            @PathVariable(name = "questionId") String questionId
    ) {
        questionCommandService.updateQuestion(questionRequest, questionId);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }

    @DeleteMapping("/{questionId}")
    @Operation(summary = "문의사항 삭제", description = "문의사항 삭제 API입니다.")
    public ResponseEntity<SuccessCode> deleteQuestion(
            @PathVariable(name = "questionId") String questionId
    ) {
        questionCommandService.deleteQuestion(questionId);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }
}
