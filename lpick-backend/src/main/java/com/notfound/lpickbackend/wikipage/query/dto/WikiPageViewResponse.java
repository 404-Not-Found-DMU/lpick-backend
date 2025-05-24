package com.notfound.lpickbackend.wikipage.query.dto;

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
