package com.notfound.lpickbackend.community.command.application.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.community.command.application.service.ArticleLikeCommandService;
import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/article")
@RequiredArgsConstructor
@Tag(name = "게시글 좋아요 컨트롤러", description = "게시글 추가/삭제 기능")
public class ArticleLikeCommandController {

    private final ArticleLikeCommandService articleLikeCommandService;

    // 북마크 생성
    @PostMapping("/{articleId}/like")
    @Operation(summary = "게시글 좋아요 ", description = "게시글 좋아요 추가하는 기능")
    public ResponseEntity<SuccessCode> createArticleLike(
            @PathVariable String articleId,
            @AuthenticationPrincipal OAuth2UserDetails userDetail
    ) {

        articleLikeCommandService.createArticleLike(articleId);

        return ResponseEntity.ok(SuccessCode.LIKE_CREATE_SUCCESS);
    }

    // 북마크 제거
    @DeleteMapping("/{articleId}/like")
    @Operation(summary = "게시글 좋아요 취소", description = "추가된 좋아요를 취소하는 기능")
    public ResponseEntity<SuccessCode> deleteArticleLike(
            @PathVariable String articleId
    ) {

        articleLikeCommandService.deleteArticleLike(articleId);

        return ResponseEntity.ok(SuccessCode.LIKE_DELETE_SUCCESS);
    }
}
