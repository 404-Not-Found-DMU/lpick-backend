package com.notfound.lpickbackend.community.command.application.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.community.command.application.dto.CommentCreate;
import com.notfound.lpickbackend.community.command.application.dto.CommentUpdate;
import com.notfound.lpickbackend.community.command.application.service.CommentCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/community")
@RequiredArgsConstructor
@Tag(name = "커뮤니티 댓글 컨트롤러", description = "댓글 생성/수정/삭제 기능")
public class CommentCommandController {

    private final CommentCommandService commentCommandService;

    @PostMapping("/{articleId}/comment")
    @Operation(summary = "댓글 작성", description = "커뮤니티 게시글에 댓글을 작성하는 기능")
    ResponseEntity<SuccessCode> createComment(
            @RequestBody CommentCreate commentCreate,
            @PathVariable("articleId") String articleId
            ){

        commentCommandService.createComment(articleId, commentCreate);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }

    @PatchMapping("/comment/{commentId}")
    @Operation(summary = "댓글 수정", description = "내가 작성한 댓글을 수정하는 기능")
    ResponseEntity<SuccessCode> updateComment(
            @PathVariable("commentId") String commentId,
            @RequestBody CommentUpdate commentUpdate
    ){
        commentCommandService.updateComment(commentId, commentUpdate);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }

    @DeleteMapping("/comment/{commentId}")
    @Operation(summary = "댓글 삭제", description = "내가 작성한 댓글을 삭제하는 기능")
    ResponseEntity<SuccessCode> deleteComment(
            @PathVariable("commentId") String commentId
    ) {
        commentCommandService.deleteComment(commentId);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }

    @PostMapping("/{articleId}/comment/{commentId}")
    @Operation(summary = "대댓글 작성", description = "댓글에 대댓글을 작성하는 기능")
    ResponseEntity<SuccessCode> createChildComment(
            @PathVariable("commentId") String commentId,
            @RequestBody CommentCreate commentCreate
    ) {
        commentCommandService.createChildComment(commentId, commentCreate);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }
}
