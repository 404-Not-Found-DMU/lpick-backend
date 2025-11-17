package com.notfound.lpickbackend.wiki.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class WikiLikeResponse {
    private boolean isLiked;
    private String wikiLikeId;

}
