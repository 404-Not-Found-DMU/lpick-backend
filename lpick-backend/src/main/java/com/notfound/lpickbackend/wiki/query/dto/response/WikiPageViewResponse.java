package com.notfound.lpickbackend.wiki.query.dto.response;

import com.notfound.lpickbackend.wiki.command.application.domain.WikiPageClass;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.Instant;

@Getter
@EqualsAndHashCode
public class WikiPageViewResponse {
    @Builder
    public WikiPageViewResponse(String wikiId, String title, String content, Instant modifiedAt, String bookmarkId, WikiPageClass wikiPageClass) {
        this.wikiId = wikiId;
        this.title = title;
        this.content = content;
        this.modifiedAt = modifiedAt;
        this.bookmarkId = bookmarkId;
        this.wikiPageClass = wikiPageClass.name(); // builder 기반 작성 시, 기입 자체는 Enum 형식으로 고정해 받아오기 위함.
    }

    private String wikiId;
    private String wikiPageClass;
    private String title;
    private String content;
    private Instant modifiedAt;
    private String bookmarkId; // bookmark가 되어있지 않으면 null로 반환
}
