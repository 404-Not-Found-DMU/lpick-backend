package com.notfound.lpickbackend.wiki.query.service;

import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiBookmark;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wiki.query.dto.response.WikiPageViewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WikiDomainQueryService {
    private final WikiPageQueryService wikiPageQueryService;
    private final PageRevisionQueryService pageRevisionQueryService;
    private final WikiBookmarkQueryService wikiBookmarkQueryService;


    public WikiPageViewResponse getWikiPageView(String wikiId, String userId) {
        WikiPage wikiPage = wikiPageQueryService.getWikiPageById(wikiId);

        PageRevision pageRevision = pageRevisionQueryService.getPageRevisionById(wikiPage.getCurrentRevision());

        Optional<WikiBookmark> bookmarkOptional = wikiBookmarkQueryService.findByWiki_WikiIdAndOauth_oauthId(wikiId, userId);

        return this.toViewResponse(wikiPage, pageRevision, bookmarkOptional);
    }

    private WikiPageViewResponse toViewResponse(WikiPage wikiEntity, PageRevision revisionEntity, Optional<WikiBookmark> bookmarkOptionalEntity) {
        return WikiPageViewResponse.builder()
                .title(wikiEntity.getTitle())
                .content(revisionEntity.getContent())
                .modifiedAt(revisionEntity.getCreatedAt())
                .bookmarkId(bookmarkOptionalEntity.map(WikiBookmark::getWikiBookmarkId).orElse(null)) // 존재하면 id값 기입, 없으면 null 기입
                .build();
    }

}
