package com.notfound.lpickbackend.wiki.command.application.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPageClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class WikiPageCreateRequestDTO {

    // 문서 제목
    String title;

    WikiPageClass wikiPageClass;

    // 문서 내용
    JsonNode content;
}
