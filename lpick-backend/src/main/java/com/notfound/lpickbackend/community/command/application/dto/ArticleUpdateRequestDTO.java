package com.notfound.lpickbackend.community.command.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleUpdateRequestDTO {

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    @NotBlank
    private String content;
}
