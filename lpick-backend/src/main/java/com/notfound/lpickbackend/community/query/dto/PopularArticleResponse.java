package com.notfound.lpickbackend.community.query.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class PopularArticleResponse {

    private String articleId;

    private String title;

    private Instant createdAt;

    private Instant modifiedAt;

    private Long likeCount;

    private Long commentCount;

    private Long bookmarkCount;

    private String oauthId;

    private String author;

    private long viewCount;

    public PopularArticleResponse(ArticleListResponse articleListResponse, long viewCount) {
        this.articleId = articleListResponse.getArticleId();
        this.title = articleListResponse.getTitle();
        this.createdAt = articleListResponse.getCreatedAt();
        this.modifiedAt = articleListResponse.getModifiedAt();
        this.likeCount = articleListResponse.getLikeCount();
        this.commentCount = articleListResponse.getCommentCount();
        this.bookmarkCount = articleListResponse.getBookmarkCount();
        this.oauthId = articleListResponse.getOauthId();
        this.author = articleListResponse.getAuthor();
        this.viewCount = viewCount;
    }
}
