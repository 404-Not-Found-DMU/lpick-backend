package com.notfound.lpickbackend.Wiki.Command.Application.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PageRevisionRequest {
    private String wikiId;
    private String content;
}
