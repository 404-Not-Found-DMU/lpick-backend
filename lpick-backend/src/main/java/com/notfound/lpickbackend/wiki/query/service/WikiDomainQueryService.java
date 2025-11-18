package com.notfound.lpickbackend.wiki.query.service;

import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
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

    public WikiPageViewResponse getWikiPageView(String wikiId, OAuth2UserDetails userDetail) {
        WikiPage wikiPage = wikiPageQueryService.getWikiPageById(wikiId);

        PageRevision pageRevision = pageRevisionQueryService.findByPageRevision_revisionNumberAndWiki_wikiId(wikiPage.getCurrentRevision(), wikiId);


        // 로그인 시 - 소유 유무에 따라 Optional 형식 반환
        // 비로그인 시  - 무조건 empty
        Optional<WikiBookmark> bookmarkOptional =
                userDetail != null ?
                        wikiBookmarkQueryService.findByWiki_WikiIdAndOauth_oauthId(wikiId, userDetail.getUsername())
                : Optional.empty();

        // 페이지 단위가 아닌, wiki 컴포넌트 기준으로 반환하도록 수정. 위키 리뷰 목록은 별도의 요청을 이미 소유하고있음.
//        Page<ReviewResponse> reviewList = wikiReviewQueryService.getReviewResponseListInWiki(
//                PageRequest.of(0, 10, Sort.by("createdAt").descending()), wikiId);
        return this.toViewResponse(wikiPage, pageRevision, bookmarkOptional);
    }
    
    // 최근에 수정된 wikiPage 10개의 리스트를 제공. '최근 수정된 위키문서' 란에 표기하기위한 목적
    // 장르별(앨범(힙합, 재즈 등), 음향기기(턴테이블, 스피커 등), 아티스트 등) 상세 READ는 추후 구현
    public List<WikiPageTitleResponse> getRecentlyModifiedWikiPageList(int pageAmount, Instant now) {
        List<PageRevision> revisionList = pageRevisionQueryService.getLatestRevisionPerWiki(
                PageRequest.of(0, pageAmount)
        );

        return revisionList.stream()
                .map(rev -> {
                    WikiPage wiki = rev.getWiki();
                    return WikiPageTitleResponse.builder()
                            .wikiId(wiki.getWikiId())
                            .title(wiki.getTitle())
                            .modifiedBefore(TimeAgoUtil.toTimeAgo(rev.getCreatedAt(), now))
                            .wikiPageClass(wiki.getWikiClass())
                            .build();
                })
                .toList();
    }

    public WikiPageViewResponse getRandomWikiPageView(OAuth2UserDetails userDetail) {

        WikiPage wikiPage = wikiPageQueryService.getRandomWikiPage();

        PageRevision pageRevision = pageRevisionQueryService.findByPageRevision_revisionNumberAndWiki_wikiId(wikiPage.getCurrentRevision(), wikiPage.getWikiId());


        // 로그인 시 - 소유 유무에 따라 Optional 형식 반환
        // 비로그인 시  - 무조건 empty
        Optional<WikiBookmark> bookmarkOptional =
                userDetail != null ?
                        wikiBookmarkQueryService.findByWiki_WikiIdAndOauth_oauthId(wikiPage.getWikiId(), userDetail.getUsername())
                        : Optional.empty();

        // 페이지 단위가 아닌, wiki 컴포넌트 기준으로 반환하도록 수정. 위키 리뷰 목록은 별도의 요청을 이미 소유하고있음.
//        Page<ReviewResponse> reviewList = wikiReviewQueryService.getReviewResponseListInWiki(
//                PageRequest.of(0, 10, Sort.by("createdAt").descending()), wikiId);
        return this.toViewResponse(wikiPage, pageRevision, bookmarkOptional);
    }

    private WikiPageViewResponse toViewResponse(WikiPage wikiEntity, PageRevision revisionEntity, Optional<WikiBookmark> bookmarkOptionalEntity) {
        return WikiPageViewResponse.builder()
                .wikiId(wikiEntity.getWikiId())
                .title(wikiEntity.getTitle())
                .content(revisionEntity.getContent())
                .modifiedAt(revisionEntity.getCreatedAt())
                .bookmarkId(bookmarkOptionalEntity.map(WikiBookmark::getWikiBookmarkId).orElse(null)) // 존재하면 id값 기입, 없으면 null 기입
                .wikiPageClass(wikiEntity.getWikiClass())
                .build();
    }
}
