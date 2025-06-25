package com.notfound.lpickbackend.community.command.application.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.community.command.application.dto.ArticleCreateRequestDTO;
import com.notfound.lpickbackend.community.command.application.dto.ArticleUpdateRequestDTO;
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
            @RequestBody ArticleCreateRequestDTO articleCreateRequestDTO
            ){

        articleCommandService.createArticle(articleCreateRequestDTO);

        return ResponseEntity.ok(SuccessCode.ARTICLE_CREATE_SUCCESS);
    }

    @PutMapping("/{articleId}")
    public ResponseEntity<SuccessCode> updateArticle(
            @PathVariable String articleId,
            @RequestBody ArticleUpdateRequestDTO articleUpdateRequestDTO
    ) {

        articleCommandService.updateArticle(articleId, articleUpdateRequestDTO);

        return ResponseEntity.ok(SuccessCode.ARTICLE_UPDATE_SUCESS);
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<SuccessCode> deleteArticle(
            @PathVariable String articleId
    ) {

        articleCommandService.deleteArticle(articleId);

        return ResponseEntity.ok(SuccessCode.ARTICLE_DELETE_SUCCESS);
    }
}
