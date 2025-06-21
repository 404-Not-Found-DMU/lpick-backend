package com.notfound.lpickbackend.community.command.application.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.community.command.application.service.ArticleLikeCommandService;
import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/article")
@RequiredArgsConstructor
public class ArticleLikeCommandController {

    private final ArticleLikeCommandService articleLikeCommandService;

    // 북마크 생성
    @PostMapping("/{articleId}/like")
    public ResponseEntity<SuccessCode> createArticleBookmark(
            @PathVariable String articleId,
            @AuthenticationPrincipal OAuth2UserDetails userDetail
    ) {

        articleLikeCommandService.createArticleLike(articleId);

        return ResponseEntity.ok(SuccessCode.BOOKMARK_CREATE_SUCCESS);
    }

    // 북마크 제거
    @DeleteMapping("/{articleId}/like")
    public ResponseEntity<SuccessCode> deleteArticleBookmark(
            @PathVariable String articleId
    ) {

        articleLikeCommandService.deleteArticleLike(articleId);

        return ResponseEntity.ok(SuccessCode.BOOKMARK_DELETE_SUCCESS);
    }
}
