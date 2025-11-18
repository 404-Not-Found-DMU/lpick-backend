package com.notfound.lpickbackend.community.query.controller;

import com.notfound.lpickbackend.community.query.dto.ArticleBookmarkListResponse;
import com.notfound.lpickbackend.community.query.dto.CommentListResponse;
import com.notfound.lpickbackend.community.query.service.ArticleBookmarkQueryService;
import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import com.notfound.lpickbackend.servicedata.query.inherenceEnum.CommentListFilter;
import com.notfound.lpickbackend.wiki.query.dto.response.WikiPageBookmarkListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/article")
@Tag(name = "게시글 북마크 조회 컨트롤러", description = "게시글 북마크 조회 컨트롤러")
public class ArticleBookmarkQueryController {
    private final ArticleBookmarkQueryService articleBookmarkQueryService;

    @GetMapping("/book-mark-list")
    @Operation(summary = "게시글 북마크 목록 조회", description = "사용자의 게시글 북마크 목록을 조회하는 기능")
    public ResponseEntity<Page<ArticleBookmarkListResponse>> getArticleBookmarkList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal OAuth2UserDetails userDetail
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ArticleBookmarkListResponse> bookmarkList =
                articleBookmarkQueryService.getArticleBookmarkListByOauthId(userDetail.getUsername(), pageable);

        return ResponseEntity.ok().body(bookmarkList);
    }
}
