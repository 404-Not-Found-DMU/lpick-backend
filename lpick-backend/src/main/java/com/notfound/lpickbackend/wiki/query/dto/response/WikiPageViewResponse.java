package com.notfound.lpickbackend.wiki.query.dto.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.Instant;

@Getter
@EqualsAndHashCode
public class WikiPageViewResponse {
    @Builder
    public WikiPageViewResponse(String wikiId, String title, String content, Instant modifiedAt, String bookmarkId, Page<ReviewResponse> reviewList) {
        this.wikiId = wikiId;
        this.title = title;
        this.content = content;
        this.modifiedAt = modifiedAt;
        this.bookmarkId = bookmarkId;
        this.reviewList = reviewList;
    }

    private String wikiId;
    private String title;
    private String content;
    private Instant modifiedAt;
    private String bookmarkId; // bookmark가 되어있지 않으면 null로 반환
    private Page<ReviewResponse> reviewList;
}
