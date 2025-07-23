package com.notfound.lpickbackend.community.command.application.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.community.command.application.dto.CommentCreate;
import com.notfound.lpickbackend.community.command.application.service.CommentCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/community/comment")
@RequiredArgsConstructor
public class CommentCommandController {

    CommentCommandService commentCommandService;

    @PostMapping
    ResponseEntity<SuccessCode> createComment(
            @RequestBody CommentCreate commentCreate
            ){

        commentCommandService.createComment(commentCreate);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }
}
