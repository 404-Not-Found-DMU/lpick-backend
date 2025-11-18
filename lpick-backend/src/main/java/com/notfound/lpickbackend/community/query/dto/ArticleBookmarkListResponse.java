package com.notfound.lpickbackend.community.query.dto;


public record ArticleBookmarkListResponse(
        String writerName,
        String articleCreatedAt,
        String articleTitle,
        String articleContent,
        String articleId,
        long likeCount,
        long viewCount
) {
}
