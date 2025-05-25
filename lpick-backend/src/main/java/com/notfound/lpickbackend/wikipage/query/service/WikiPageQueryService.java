package com.notfound.lpickbackend.wikipage.query.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
import com.notfound.lpickbackend.wiki.query.service.PageRevisionQueryService;
import com.notfound.lpickbackend.wikipage.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wikipage.query.dto.WikiPageViewResponse;
import com.notfound.lpickbackend.wikipage.query.repository.WikiPageQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WikiPageQueryService {
    private final PageRevisionQueryService pageRevisionQueryService;

    private final WikiPageQueryRepository wikiPageQueryRepository;

    public WikiPageViewResponse getWikiPageView(String wikiId) {
        WikiPage wikiPage = getWikiPageById(wikiId);

        PageRevision pageRevision = pageRevisionQueryService.getPageRevisionById(wikiPage.getCurrentRevision());

        return this.toViewResponse(wikiPage, pageRevision);
    }

    public WikiPage getWikiPageById(String wikiId) {
        return wikiPageQueryRepository.findById(wikiId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_WIKI));
    }


    private WikiPageViewResponse toViewResponse(WikiPage wikiEntity, PageRevision revisionEntity) {
        return WikiPageViewResponse.builder()
                .title(wikiEntity.getTitle())
                .content(revisionEntity.getContent())
                .modifiedAt(revisionEntity.getCreatedAt())
                .build();
    }
}
