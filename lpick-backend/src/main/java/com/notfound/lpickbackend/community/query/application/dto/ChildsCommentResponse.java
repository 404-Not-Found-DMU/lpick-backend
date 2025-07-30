package com.notfound.lpickbackend.community.query.application.dto;

import com.notfound.lpickbackend.community.command.domain.CommentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ChildsCommentResponse {

    private String commentId;

    private String content;

    private Instant createdAt;

    private Instant modifiedAt;

    private String articleId;

    private String parentCommentId;

    private String oauthId;

    private boolean liked;

    private int likeCount;
}
