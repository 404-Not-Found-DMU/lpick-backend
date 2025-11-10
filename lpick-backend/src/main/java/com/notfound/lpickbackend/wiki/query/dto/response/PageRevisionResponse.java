package com.notfound.lpickbackend.wiki.query.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserIdNamePairResponse;
import lombok.*;

import java.time.Instant;

@Getter
@EqualsAndHashCode // 테스트 비교목적
public class PageRevisionResponse {
    @Builder
    public PageRevisionResponse(String revisionId, String revisionNumber, JsonNode content, Instant createdAt, UserIdNamePairResponse createWho) {
        this.revisionId = revisionId;
        this.revisionNumber = revisionNumber;
        this.content = content;
        this.createdAt = createdAt;
        this.createWho = createWho;
    }

    private String revisionId;
    private String revisionNumber;
    private JsonNode content;
    private Instant createdAt;
    private UserIdNamePairResponse createWho;
}
