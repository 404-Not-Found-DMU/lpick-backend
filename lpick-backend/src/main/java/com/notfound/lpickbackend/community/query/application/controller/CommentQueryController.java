package com.notfound.lpickbackend.community.query.application.controller;

import com.notfound.lpickbackend.community.query.application.dto.ArticleListResponse;
import com.notfound.lpickbackend.community.query.application.dto.CommentListResponse;
import com.notfound.lpickbackend.community.query.application.dto.ParentsCommentResponse;
import com.notfound.lpickbackend.community.query.application.service.CommentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/community/comment")
@RequiredArgsConstructor
public class CommentQueryController {

    private final CommentQueryService commentQueryService;

    @GetMapping("/{articleId}")
    public ResponseEntity<Page<ParentsCommentResponse>> getComments(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @PathVariable("articleId") String articleId
    ) {

        Pageable pageable = PageRequest.of(page - 1, size);

        return ResponseEntity.ok(commentQueryService.readCommentList(articleId, pageable));
    }
}
