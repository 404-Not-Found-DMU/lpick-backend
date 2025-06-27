package com.notfound.lpickbackend.community.query.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ArticleListResponseDTO {

    private String articleId;

    private String title;

    private Long likeCount;

    private Long commentCount;

    private Long bookmarkCount;

    private String oauthId;
}
