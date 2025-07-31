package com.notfound.lpickbackend.community.query.controller;

import com.notfound.lpickbackend.community.query.dto.ParentsCommentResponse;
import com.notfound.lpickbackend.community.query.service.CommentQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/community/comment")
@RequiredArgsConstructor
@Tag(name = "커뮤니티 댓글 조회 컨트롤러", description = "댓글 조회, 내 댓글 조회 기능")
public class CommentQueryController {

    private final CommentQueryService commentQueryService;

    @GetMapping("/{articleId}")
    @Operation(summary = "댓글 목록 조회", description = "특정 게시글의 댓글 목록을 조회합니다.")
    public ResponseEntity<Page<ParentsCommentResponse>> getComments(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @PathVariable("articleId") String articleId
    ) {

        Pageable pageable = PageRequest.of(page - 1, size);

        return ResponseEntity.ok(commentQueryService.readCommentList(articleId, pageable));
    }

    @GetMapping("/parents/like")
    @Operation(summary = "좋아요 댓글 조회", description = "내가 좋아요 누른 댓글 목록을 조회합니다.")
    public ResponseEntity<Page<ParentsCommentResponse>> getLikedParentsComments(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);

        return ResponseEntity.ok(commentQueryService.readLikedParentsCommentList(pageable));
    }

    @GetMapping("/child/like")
    @Operation(summary = "좋아요 답글 조회", description = "내가 좋아요 누른 답글 목록을 조회합니다.")
    public ResponseEntity<Page<ParentsCommentResponse>> getLikedChildComments(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);

        return ResponseEntity.ok(commentQueryService.readLikedChildCommentList(pageable));
    }

}
