package com.notfound.lpickbackend.support.command.application.controller;

import com.notfound.lpickbackend.support.command.application.service.AnswerCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/answer")
@Tag(name = "", description = "")
public class AnswerCommandController {

     private final AnswerCommandService answerCommandService;

    @GetMapping
    @Operation(summary = "테스트 API", description = "AnswerCommand 기본 테스트 API입니다.")
    public String test() {
        return "ok";
    }
}
