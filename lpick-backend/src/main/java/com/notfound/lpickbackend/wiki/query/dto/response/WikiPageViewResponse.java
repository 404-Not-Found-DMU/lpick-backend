package com.notfound.lpickbackend.wiki.query.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class WikiPageViewResponse {
    @Builder
    public WikiPageViewResponse(String title, String content, Instant modifiedAt, String bookmarkId) {
        this.title = title;
        this.content = content;
        this.modifiedAt = modifiedAt;
        this.bookmarkId = bookmarkId;
    }

    private String title;
    private String content;
    private Instant modifiedAt;
    private String bookmarkId; // bookmark가 되어있지 않으면 null로 반환
}
