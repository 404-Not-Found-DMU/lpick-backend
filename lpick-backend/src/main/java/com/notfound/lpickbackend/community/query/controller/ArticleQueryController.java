package com.notfound.lpickbackend.community.query.controller;

import com.notfound.lpickbackend.community.query.dto.ArticleDetailResponse;
import com.notfound.lpickbackend.community.query.dto.ArticleListResponse;
import com.notfound.lpickbackend.community.query.service.ArticleQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community")
@Tag(name = "게시글 조회 컨트롤러", description = "게시글 목록/상세 조회 기능")
public class ArticleQueryController {

    private final ArticleQueryService articleQueryService;

    @GetMapping("/article")
    @Operation(summary = "모든 게시글 목록 조회", description = "모든 커뮤니티 게시글 목록을 페이지 단위로 조회하는 기능")
    public ResponseEntity<Page<ArticleListResponse>> readAllArticleList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ){

        Pageable pageable = PageRequest.of(page - 1, size);

        return ResponseEntity.ok(articleQueryService.readAllArticleList(pageable));
    }

    @GetMapping("/article/{articleId}")
    @Operation(summary = "게시글 상세조회", description = "특정 게시글의 상세 내역을 조회하는 기능")
    public ResponseEntity<ArticleDetailResponse> readArticleDetail(
            @PathVariable("articleId") String articleId
    ){
        return ResponseEntity.ok(articleQueryService.readArticleDetail(articleId));
    }

    @GetMapping("/article/me")
    @Operation(summary = "내 게시글 조회", description = "내가 작성한 게시글을 페이지 단위로 조회하는 기능")
    public ResponseEntity<Page<ArticleListResponse>> readMyArticleList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page - 1, size);

        return ResponseEntity.ok(articleQueryService.readMyArticleList(pageable));
    }

    @GetMapping("/article/like/me")
    @Operation(summary = "좋아요 게시글 조회", description = "내가 좋아요 누른 게시글 목록을 조회하는 기능")
    public ResponseEntity<Page<ArticleListResponse>> readMyLikedArticleList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page - 1, size);

        return ResponseEntity.ok(articleQueryService.readMyLikedArticleList(pageable));
    }
}
