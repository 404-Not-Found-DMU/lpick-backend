package com.notfound.lpickbackend.community.query.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleDetailResponseDTO {

    private String articleId;

    private String title;

    private String content;

    private Long likeCount;

    private Long commentCount;

    private Long bookmarkCount;

    private String oauthId;

    private boolean liked; // 조회 요청한 사람이 좋아요 눌렀는지

    private boolean bookmarked; // 조회 요청한 사람이 북마크 했는지

    // liked 와 bookmarked 를 제외한 생성자. service 로직에서 추가로 채울것.
    public ArticleDetailResponseDTO(
            String articleId,
            String title,
            String content,
            Long likeCount,
            Long commentCount,
            Long bookmarkCount,
            String oauthId
    ) {
        this.articleId = articleId;
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.bookmarkCount = bookmarkCount;
        this.oauthId = oauthId;
    }
}
