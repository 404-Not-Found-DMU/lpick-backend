package com.notfound.lpickbackend.wiki.command.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PageRevisionRequest {
    @NotEmpty
    private String wikiId;

    @NotEmpty
    private String content;
}
