package com.notfound.lpickbackend.community.command.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ArticleCreateRequestDTO {

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    @NotBlank
    private String content;
}
