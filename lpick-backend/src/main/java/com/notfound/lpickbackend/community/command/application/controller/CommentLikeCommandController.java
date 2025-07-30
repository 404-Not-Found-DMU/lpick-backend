package com.notfound.lpickbackend.community.command.application.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.community.command.application.service.CommentLikeCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/comment/like")
@Tag(name = "커뮤니티 댓글 좋아요 컨트롤러", description = "댓글 좋아요 추가/취소 기능")
public class CommentLikeCommandController {

    private final CommentLikeCommandService commentLikeCommandService;

    @PostMapping("/{commentId}")
    @Operation(summary = "댓글 좋아요 추가", description = "특정 댓글에 좋아요를 추가하는 기능")
    ResponseEntity<SuccessCode> createCommentLike(
            @PathVariable("commentId") String commentId
    ){

        commentLikeCommandService.createCommentLike(commentId);

        return ResponseEntity.ok(SuccessCode.LIKE_CREATE_SUCCESS);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 좋아요 취소", description = "추가된 댓글 좋아요를 취소하는 기능")
    ResponseEntity<SuccessCode> deleteCommentLike(
            @PathVariable("commentId") String commentId
    ) {

        commentLikeCommandService.deleteCommentLike(commentId);

        return ResponseEntity.ok(SuccessCode.LIKE_DELETE_SUCCESS);
    }
}
