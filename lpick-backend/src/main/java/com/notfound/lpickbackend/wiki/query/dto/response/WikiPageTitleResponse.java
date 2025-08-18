package com.notfound.lpickbackend.wiki.query.dto.response;

import com.notfound.lpickbackend.wiki.command.application.domain.WikiPageClass;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
public class WikiPageTitleResponse {
    @Builder
    public WikiPageTitleResponse(String wikiId, String title, String modifiedBefore, WikiPageClass wikiPageClass) {
        this.wikiId = wikiId;
        this.title = title;
        this.modifiedBefore = modifiedBefore;
        this.wikiPageClass = wikiPageClass.name();
    }

    private String wikiId;
    private String title;
    private String modifiedBefore; // 현재 시각을 기준으로 몇 초 전에 해당 문서가 수정되었는지 표기
    private String wikiPageClass;
}
