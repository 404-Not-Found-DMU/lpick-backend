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

        PageRevision pageRevision = pageRevisionQueryService.findByPageRevision_revisionNumberAndWiki_wikiId(wikiPage.getCurrentRevision(), wikiId);

        Optional<WikiBookmark> bookmarkOptional = wikiBookmarkQueryService.findByWiki_WikiIdAndOauth_oauthId(wikiId, userId);

        Page<ReviewResponse> reviewList = wikiReviewQueryService.getReviewResponseListInWiki(
                PageRequest.of(0, 10, Sort.by("createdAt").descending()), wikiId);
        return this.toViewResponse(wikiPage, pageRevision, bookmarkOptional, reviewList);
    }
    
    // 최근에 수정된 wikiPage 10개의 리스트를 제공. '최근 수정된 위키문서' 란에 표기하기위한 목적
    // 장르별(앨범(힙합, 재즈 등), 음향기기(턴테이블, 스피커 등), 아티스트 등) 상세 READ는 추후 구현
    public List<WikiPageTitleResponse> getRecentlyModifiedWikiPageList(int pageAmount, Instant now) {
        List<PageRevision> revisionList = pageRevisionQueryService.getLatestRevisionPerWiki(
                PageRequest.of(0, pageAmount)
        ).getContent();

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
                .wikiId(wikiEntity.getWikiId())
                .title(wikiEntity.getTitle())
                .content(revisionEntity.getContent())
                .modifiedAt(revisionEntity.getCreatedAt())
                .bookmarkId(bookmarkOptionalEntity.map(WikiBookmark::getWikiBookmarkId).orElse(null)) // 존재하면 id값 기입, 없으면 null 기입
                .reviewList(reviewResponses)
                .build();
    }
}
