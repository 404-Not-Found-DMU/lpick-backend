package com.notfound.lpickbackend.community.command.application.controller;

import com.notfound.lpickbackend.common.elasticsearch.service.DataSyncService;
import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.community.command.application.dto.ArticleCreateRequest;
import com.notfound.lpickbackend.community.command.application.dto.ArticleUpdateRequest;
import com.notfound.lpickbackend.community.command.application.service.ArticleCommandService;
import com.notfound.lpickbackend.community.command.domain.Article;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/community/article")
@RequiredArgsConstructor
@Tag(name = "게시글 컨트롤러", description = "게시글 생성/수정/삭제 관련 기능")
public class ArticleCommandController {

    private final ArticleCommandService articleCommandService;
    private final DataSyncService dataSyncService;

    @PostMapping
    @Operation(summary = "커뮤니티 게시글 생성", description = "커뮤니티 게시글을 새로 생성하는 기능")
    public ResponseEntity<SuccessCode> createArticle(
            @RequestBody ArticleCreateRequest articleCreateRequest
            ){

        Article article = articleCommandService.createArticle(articleCreateRequest);

        dataSyncService.syncArticle(article);

        return ResponseEntity.ok(SuccessCode.ARTICLE_CREATE_SUCCESS);
    }

    @PutMapping("/{articleId}")
    @Operation(summary = "커뮤니티 게시글 수정", description = "내가 작성한 커뮤니티 게시글을 수정하는 기능")
    public ResponseEntity<SuccessCode> updateArticle(
            @PathVariable("articleId") String articleId,
            @RequestBody ArticleUpdateRequest articleUpdateRequest
    ) {

        articleCommandService.updateArticle(articleId, articleUpdateRequest);

        return ResponseEntity.ok(SuccessCode.ARTICLE_UPDATE_SUCESS);
    }

    @DeleteMapping("/{articleId}")
    @Operation(summary = "커뮤니티 게시글 삭제", description = "내가 작성한 커뮤니티 게시글을 삭제하는 기능")
    public ResponseEntity<SuccessCode> deleteArticle(
            @PathVariable("articleId") String articleId
    ) {

        articleCommandService.deleteArticle(articleId);

        return ResponseEntity.ok(SuccessCode.ARTICLE_DELETE_SUCCESS);
    }
}
