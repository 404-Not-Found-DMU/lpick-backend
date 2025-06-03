package com.notfound.lpickbackend.wiki.query.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class WikiPageTitleResponse {
    private String wikiId;
    private String title;
    private String modifiedBefore; // 현재 시각을 기준으로 몇 초 전에 해당 문서가 수정되었는지 표기
}
