package com.notfound.lpickbackend.community.query.application.controller;

import com.notfound.lpickbackend.community.query.application.dto.ArticleListResponse;
import com.notfound.lpickbackend.community.query.application.dto.CommentListResponse;
import com.notfound.lpickbackend.community.query.application.service.CommentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/community/comment")
@RequiredArgsConstructor
public class CommentQueryController {

    private final CommentQueryService commentQueryService;

    @GetMapping
    public ResponseEntity<Page<CommentListResponse>> getComments(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {



        return null;
    }
}
