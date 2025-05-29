package com.notfound.lpickbackend.wiki.query.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class WikiPageViewResponse {
    @Builder
    public WikiPageViewResponse(String title, String content, Instant modifiedAt) {
        this.title = title;
        this.content = content;
        this.modifiedAt = modifiedAt;
    }

    private String title;
    private String content;
    private Instant modifiedAt;

}
