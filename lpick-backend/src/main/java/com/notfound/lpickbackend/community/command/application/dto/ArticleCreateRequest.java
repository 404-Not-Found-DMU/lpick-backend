package com.notfound.lpickbackend.community.command.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleCreateRequest {

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    @NotBlank
    private String content;
}
