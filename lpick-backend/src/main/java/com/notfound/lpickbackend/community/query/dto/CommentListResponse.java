package com.notfound.lpickbackend.community.query.dto;


import java.time.Instant;

public record CommentListResponse(
        String commentValue,
        Instant createdAt,
        String articleId,
        String articleTitle,
        String articleWriterName,
        long commentLikeCount
) {
}
