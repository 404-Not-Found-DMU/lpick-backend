package com.notfound.lpickbackend.wiki.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WikiPageBookmarkListResponse {
    private String wikiBookmarkId;
    private String wikiPageId;
    private String wikiTitle;
}
