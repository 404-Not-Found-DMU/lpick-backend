package com.notfound.lpickbackend.community.command.application.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.community.command.application.dto.CommentCreate;
import com.notfound.lpickbackend.community.command.application.dto.CommentUpdate;
import com.notfound.lpickbackend.community.command.application.service.CommentCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/{commentId}")
    ResponseEntity<SuccessCode> updateComment(
            @PathVariable String commentId,
            @RequestBody CommentUpdate commentUpdate
    ){
        commentCommandService.updateComment(commentUpdate);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }

    @DeleteMapping("/{commentId}")
    ResponseEntity<SuccessCode> deleteComment(
            @PathVariable String commentId
    ) {
        commentCommandService.deleteComment(commentId);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }

    @PostMapping("/{commentId}")
    ResponseEntity<SuccessCode> createChildComment(
            @PathVariable String commentId,
            @RequestBody CommentCreate commentCreate
    ) {
        commentCommandService.createChildComment(commentId, commentCreate);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }
}
