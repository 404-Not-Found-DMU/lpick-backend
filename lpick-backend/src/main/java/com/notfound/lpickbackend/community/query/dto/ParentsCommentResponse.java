package com.notfound.lpickbackend.community.query.dto;

import com.notfound.lpickbackend.community.command.domain.CommentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ParentsCommentResponse {

    private String commentId;

    private String content;

    private Instant createdAt;

    private Instant modifiedAt;

    private CommentStatus isDel;

    private String articleId;

    private String oauthId;

    private String author;

    private String authorProfile;

    private boolean liked;

    private int likeCount;

    private List<ChildsCommentResponse> childsCommentList;
}
