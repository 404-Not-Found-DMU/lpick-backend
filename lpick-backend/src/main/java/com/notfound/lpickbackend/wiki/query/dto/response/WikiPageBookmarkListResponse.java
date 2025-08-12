package com.notfound.lpickbackend.wiki.query.dto.response;

import com.notfound.lpickbackend.wiki.command.application.domain.WikiPageClass;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WikiPageBookmarkListResponse {

    @Builder
    public WikiPageBookmarkListResponse(String wikiBookmarkId, String wikiPageId, String wikiTitle, WikiPageClass wikiPageClass) {
        this.wikiBookmarkId = wikiBookmarkId;
        this.wikiPageId = wikiPageId;
        this.wikiTitle = wikiTitle;
        this.wikiPageClass = wikiPageClass.name();
    }

    private String wikiBookmarkId;
    private String wikiPageId;
    private String wikiTitle;
    private String wikiPageClass;
}
