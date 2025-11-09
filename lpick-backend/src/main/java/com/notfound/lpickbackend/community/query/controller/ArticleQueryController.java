package com.notfound.lpickbackend.community.query.controller;

import com.notfound.lpickbackend.community.query.dto.ArticleDetailResponse;
import com.notfound.lpickbackend.community.query.dto.ArticleListResponse;
import com.notfound.lpickbackend.community.query.service.ArticleQueryService;
import com.notfound.lpickbackend.servicedata.query.service.PopularityService;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPageClass;
import com.notfound.lpickbackend.wiki.query.dto.response.PopularItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "게시글 조회 컨트롤러", description = "게시글 목록/상세 조회 기능")
public class ArticleQueryController {

    private final ArticleQueryService articleQueryService;
    private static final Logger viewLog = LoggerFactory.getLogger("view-logger");
    private final PopularityService popularityService;

    @GetMapping("/public/community/article")
    @Operation(summary = "모든 게시글 목록 조회", description = "모든 커뮤니티 게시글 목록을 페이지 단위로 조회하는 기능")
    public ResponseEntity<Page<ArticleListResponse>> readAllArticleList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ){

        Pageable pageable = PageRequest.of(page - 1, size);

        return ResponseEntity.ok(articleQueryService.readAllArticleList(pageable));
    }

    @GetMapping("/public/community/article/{articleId}")
    @Operation(summary = "게시글 상세조회", description = "특정 게시글의 상세 내역을 조회하는 기능")
    public ResponseEntity<ArticleDetailResponse> readArticleDetail(
            @PathVariable("articleId") String articleId
    ){
        viewLog.info("{\"type\": \"ARTICLE\", \"id\": \"{}\"}", articleId);

        return ResponseEntity.ok(articleQueryService.readArticleDetail(articleId));
    }

    @GetMapping("/community/article/me")
    @Operation(summary = "내 게시글 조회", description = "내가 작성한 게시글을 페이지 단위로 조회하는 기능")
    public ResponseEntity<Page<ArticleListResponse>> readMyArticleList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page - 1, size);

        return ResponseEntity.ok(articleQueryService.readMyArticleList(pageable));
    }

    @GetMapping("/community/article/like/me")
    @Operation(summary = "좋아요 게시글 조회", description = "내가 좋아요 누른 게시글 목록을 조회하는 기능")
    public ResponseEntity<Page<ArticleListResponse>> readMyLikedArticleList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page - 1, size);

        return ResponseEntity.ok(articleQueryService.readMyLikedArticleList(pageable));
    }

    @Operation(
            summary = "인기 게시글 항목 조회",
            description = "최근 1시간 조회 로그를 기준으로 인기 게시글을 리턴합니다. id는 ArticleId를 의미합니다."
    )
    @GetMapping("/public/community/popular/article")
    public ResponseEntity<List<PopularItemResponse>> getPopularArticles(
            @Parameter(description = "가져올 개수", example = "5")
            @RequestParam(defaultValue = "5") int size
    ) {
        List<PopularItemResponse> result = popularityService.getPopularWikis("ARTICLE", size);
        return ResponseEntity.ok(result);
    }
}
