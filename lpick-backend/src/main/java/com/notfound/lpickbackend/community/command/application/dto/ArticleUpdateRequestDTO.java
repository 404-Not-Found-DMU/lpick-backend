package com.notfound.lpickbackend.community.command.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleUpdateRequestDTO {

    @NotNull
    private String title;

    @NotNull
    private String content;

}
