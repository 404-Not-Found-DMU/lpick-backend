package com.notfound.lpickbackend.wiki.command.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.time.Instant;

@Getter
public class ReviewPostRequest {

    @NotBlank
    private String content;

    @Min(1)
    @Max(5)
    @Positive
    private float starScore;

    @NotBlank
    private Instant createdAt;
}
