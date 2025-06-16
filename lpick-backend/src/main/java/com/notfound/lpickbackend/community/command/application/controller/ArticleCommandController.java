package com.notfound.lpickbackend.community.command.application.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.community.command.application.dto.ArticleCreateRequest;
import com.notfound.lpickbackend.community.command.application.dto.ArticleUpdateRequest;
import com.notfound.lpickbackend.community.command.application.service.ArticleCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/article")
@RequiredArgsConstructor
public class ArticleCommandController {

    private final ArticleCommandService articleCommandService;

    @PostMapping
    public ResponseEntity<SuccessCode> createArticle(
            @RequestBody ArticleCreateRequest articleCreateRequest
            ){

        articleCommandService.createArticle(articleCreateRequest);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }

    @PutMapping("/{articleId}")
    public ResponseEntity<SuccessCode> updateArticle(
            @PathVariable String articleId,
            @RequestBody ArticleUpdateRequest articleUpdateRequest
    ) {

        articleCommandService.updateArticle(articleId, articleUpdateRequest);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }
}
