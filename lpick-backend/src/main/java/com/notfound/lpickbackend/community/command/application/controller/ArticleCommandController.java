package com.notfound.lpickbackend.community.command.application.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.community.command.application.dto.ArticleCreateRequestDTO;
import com.notfound.lpickbackend.community.command.application.dto.ArticleUpdateRequestDTO;
import com.notfound.lpickbackend.community.command.application.service.ArticleCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/article")
@RequiredArgsConstructor
@Tag(name = "게시글 컨트롤러", description = "게시글 생성/수정/삭제 관련 기능")
public class ArticleCommandController {

    private final ArticleCommandService articleCommandService;

    @PostMapping
    @Operation(summary = "커뮤니티 게시글 생성", description = "커뮤니티 게시글을 새로 생성하는 기능")
    public ResponseEntity<SuccessCode> createArticle(
            @RequestBody ArticleCreateRequestDTO articleCreateRequestDTO
            ){

        articleCommandService.createArticle(articleCreateRequestDTO);

        return ResponseEntity.ok(SuccessCode.ARTICLE_CREATE_SUCCESS);
    }

    @PutMapping("/{articleId}")
    @Operation(summary = "커뮤니티 게시글 수정", description = "내가 작성한 커뮤니티 게시글을 수정하는 기능")
    public ResponseEntity<SuccessCode> updateArticle(
            @PathVariable String articleId,
            @RequestBody ArticleUpdateRequestDTO articleUpdateRequestDTO
    ) {

        articleCommandService.updateArticle(articleId, articleUpdateRequestDTO);

        return ResponseEntity.ok(SuccessCode.ARTICLE_UPDATE_SUCESS);
    }

    @DeleteMapping("/{articleId}")
    @Operation(summary = "커뮤니티 게시글 삭제", description = "내가 작성한 커뮤니티 게시글을 삭제하는 기능")
    public ResponseEntity<SuccessCode> deleteArticle(
            @PathVariable String articleId
    ) {

        articleCommandService.deleteArticle(articleId);

        return ResponseEntity.ok(SuccessCode.ARTICLE_DELETE_SUCCESS);
    }
}
