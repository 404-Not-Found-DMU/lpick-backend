package com.notfound.lpickbackend.community.query.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ArticleDetailResponse {

    private String articleId;

    private String title;

    private String content;

    private Instant createdAt;

    private Instant modifiedAt;

    private Long likeCount;

    private Long commentCount;

    private Long bookmarkCount;

    private String oauthId;

    private boolean liked; // 조회 요청한 사람이 좋아요 눌렀는지

    private boolean bookmarked; // 조회 요청한 사람이 북마크 했는지

    // liked 와 bookmarked 를 제외한 생성자. service 로직에서 추가로 채울것.
    public ArticleDetailResponse(
            String articleId,
            String title,
            String content,
            Instant createdAt,
            Instant modifiedAt,
            Long likeCount,
            Long commentCount,
            Long bookmarkCount,
            String oauthId
    ) {
        this.articleId = articleId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.bookmarkCount = bookmarkCount;
        this.oauthId = oauthId;
    }
}
