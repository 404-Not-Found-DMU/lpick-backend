package com.notfound.lpickbackend.support.command.application.controller;

import com.notfound.lpickbackend.support.command.application.service.NoticeCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notice")
@Tag(name = "", description = "")
public class NoticeCommandController {

     private final NoticeCommandService noticeCommandService;

    @GetMapping
    @Operation(summary = "테스트 API", description = "NoticeCommandController 기본 테스트 API입니다.")
    public String test() {
        return "ok";
    }
}
