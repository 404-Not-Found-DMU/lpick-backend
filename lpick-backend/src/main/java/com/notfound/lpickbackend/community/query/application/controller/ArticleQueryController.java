package com.notfound.lpickbackend.community.query.application.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.community.query.application.dto.ArticleDetailResponseDTO;
import com.notfound.lpickbackend.community.query.application.dto.ArticleListResponseDTO;
import com.notfound.lpickbackend.community.query.application.service.ArticleQueryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ArticleQueryController {

    private final ArticleQueryService articleQueryService;

    @GetMapping("/article")
    public ResponseEntity<List<ArticleListResponseDTO>> readAllArticleList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ){

        Pageable pageable = PageRequest.of(page - 1, size);

        return ResponseEntity.ok(articleQueryService.readAllArticleList(pageable));
    }

    @GetMapping("/article/{articleId}")
    public ResponseEntity<ArticleDetailResponseDTO> readArticleDetail(
            @PathVariable("articleId") String articleId
    ){
        return ResponseEntity.ok(articleQueryService.readArticleDetail(articleId));
    }

    @GetMapping("/article/me")
    public ResponseEntity<List<ArticleListResponseDTO>> readMyArticleList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page - 1, size);

        return ResponseEntity.ok(articleQueryService.readMyArticleList(pageable));
    }
}
