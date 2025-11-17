package com.notfound.lpickbackend.community.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
public class ArticleListResponse {

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
}
