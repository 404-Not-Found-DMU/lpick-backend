package com.notfound.lpickbackend.wiki.query.dto.response;

import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class PopularItemResponse {

    private String id; // 위키 ID
    private String name; // 이름 (앨범명, 아티스트명 등)
    private long viewCount; // 1시간 동안의 조회수

    public PopularItemResponse(WikiPage item, long viewCount) {
        this.id = item.getWikiId();
        this.name = item.getTitle();
        this.viewCount = viewCount;
    }
}
