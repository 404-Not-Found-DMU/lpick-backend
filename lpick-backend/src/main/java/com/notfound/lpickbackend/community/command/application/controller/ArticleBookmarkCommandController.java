package com.notfound.lpickbackend.community.command.application.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.community.command.application.service.ArticleBookmarkCommandService;
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
@Tag(name = "게시글 북마크 컨트롤러", description = "게시글 북마크 추가/삭제 기능")
public class ArticleBookmarkCommandController {

    private final ArticleBookmarkCommandService articleBookmarkCommandService;

    // 북마크 생성
    @PostMapping("/{articleId}/bookmark")
    @Operation(summary = "게시글 북마크 추가", description = "커뮤니티 게시글에 대해 북마크를 추가하는 기능")
    public ResponseEntity<SuccessCode> createArticleBookmark(
            @PathVariable String articleId,
            @AuthenticationPrincipal OAuth2UserDetails userDetail
    ) {

        articleBookmarkCommandService.createArticleBookmark(articleId);

        return ResponseEntity.ok(SuccessCode.BOOKMARK_CREATE_SUCCESS);
    }

    // 북마크 제거
    @DeleteMapping("/{articleId}/bookmark")
    @Operation(summary = "게시글 북마크 취소", description = "추가 된 게시글 북마크를 취소하는 기능")
    public ResponseEntity<SuccessCode> deleteArticleBookmark(
            @PathVariable String articleId
    ) {

        articleBookmarkCommandService.deleteArticleBookmark(articleId);

        return ResponseEntity.ok(SuccessCode.BOOKMARK_DELETE_SUCCESS);
    }
}
