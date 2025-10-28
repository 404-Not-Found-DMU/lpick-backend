package com.notfound.lpickbackend.community.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
public class ChildsCommentResponse {

    private String commentId;

    private String content;

    private Instant createdAt;

    private Instant modifiedAt;

    private String articleId;

    private String parentCommentId;

    private String oauthId;

    private String author;

    private boolean liked;

    private int likeCount;
}
