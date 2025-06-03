package com.notfound.lpickbackend.wiki.query.service;

import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiBookmark;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wiki.query.dto.response.ReviewResponse;
import com.notfound.lpickbackend.wiki.query.dto.response.WikiPageTitleResponse;
import com.notfound.lpickbackend.wiki.query.dto.response.WikiPageViewResponse;
import com.notfound.lpickbackend.wiki.query.util.TimeAgoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WikiDomainQueryService {
    private final WikiPageQueryService wikiPageQueryService;
    private final PageRevisionQueryService pageRevisionQueryService;
    private final WikiBookmarkQueryService wikiBookmarkQueryService;
    private final WikiReviewQueryService wikiReviewQueryService;

    public WikiPageViewResponse getWikiPageView(String wikiId, String userId) {
        WikiPage wikiPage = wikiPageQueryService.getWikiPageById(wikiId);

        PageRevision pageRevision = pageRevisionQueryService.getPageRevisionById(wikiPage.getCurrentRevision());

        Optional<WikiBookmark> bookmarkOptional = wikiBookmarkQueryService.findByWiki_WikiIdAndOauth_oauthId(wikiId, userId);

        Page<ReviewResponse> reviewList = wikiReviewQueryService.getReviewResponseListInWiki(
                PageRequest.of(0, 10, Sort.by("createdAt").descending()), wikiId);
        return this.toViewResponse(wikiPage, pageRevision, bookmarkOptional, reviewList);
    }

    public List<WikiPageTitleResponse> getRecentlyModifiedWikiPageList(int pageAmount) {
        List<PageRevision> revisionList = pageRevisionQueryService.getRecentlyCreatedPageRevision(
                PageRequest.of(0,
                        pageAmount,
                        Sort.by("createdAt").descending())
        ).getContent();

        Instant now = Instant.now();

        return revisionList.stream().map(i -> {
            Instant updateRevisionAt = i.getCreatedAt();
            WikiPage wikiPage = i.getWiki();
            return WikiPageTitleResponse.builder()
                    .wikiId(wikiPage.getWikiId())
                    .title(wikiPage.getTitle())
                    .modifiedBefore(TimeAgoUtil.toTimeAgo(updateRevisionAt, now))
                    .build();
        }).toList();

    }

    private WikiPageViewResponse toViewResponse(WikiPage wikiEntity, PageRevision revisionEntity, Optional<WikiBookmark> bookmarkOptionalEntity, Page<ReviewResponse> reviewResponses) {
        return WikiPageViewResponse.builder()
                .title(wikiEntity.getTitle())
                .content(revisionEntity.getContent())
                .modifiedAt(revisionEntity.getCreatedAt())
                .bookmarkId(bookmarkOptionalEntity.map(WikiBookmark::getWikiBookmarkId).orElse(null)) // 존재하면 id값 기입, 없으면 null 기입
                .reviewList(reviewResponses)
                .build();
    }
}
