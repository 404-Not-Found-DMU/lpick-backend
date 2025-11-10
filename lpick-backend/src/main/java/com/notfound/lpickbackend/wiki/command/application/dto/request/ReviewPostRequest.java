package com.notfound.lpickbackend.wiki.command.application.dto.request;

import jakarta.validation.constraints.*;
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
}
