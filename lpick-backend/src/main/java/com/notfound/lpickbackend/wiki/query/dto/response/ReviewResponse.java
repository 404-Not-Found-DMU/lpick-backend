package com.notfound.lpickbackend.wiki.query.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ReviewResponse {
    private String reviewId;
    private float starScore;
    private String content;
    private Instant createdAt;
}
