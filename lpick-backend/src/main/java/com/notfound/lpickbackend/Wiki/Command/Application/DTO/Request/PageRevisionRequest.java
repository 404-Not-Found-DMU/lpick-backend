package com.notfound.lpickbackend.Wiki.Command.Application.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PageRevisionRequest {
    private String wikiId;
    private String content;
}
