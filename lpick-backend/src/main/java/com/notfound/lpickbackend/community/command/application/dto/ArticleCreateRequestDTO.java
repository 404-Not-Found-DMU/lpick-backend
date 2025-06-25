package com.notfound.lpickbackend.community.command.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ArticleCreateRequestDTO {

    @NotNull
    private String title;

    @NotNull
    private String content;
}
