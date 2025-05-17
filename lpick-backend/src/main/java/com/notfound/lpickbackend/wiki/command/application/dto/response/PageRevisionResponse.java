package com.notfound.lpickbackend.wiki.command.application.dto.response;

import lombok.*;

import java.time.Instant;

@Getter
public class PageRevisionResponse {
    @Builder
    public PageRevisionResponse(String revisionId, String content, Instant createdAt, String createWho) {
        this.revisionId = revisionId;
        this.content = content;
        this.createdAt = createdAt;
        this.createWho = createWho;
    }

    private String revisionId;
    private String content;
    private Instant createdAt;
    private String createWho;
}
