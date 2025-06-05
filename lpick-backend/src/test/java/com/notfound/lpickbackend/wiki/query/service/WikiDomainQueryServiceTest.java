package com.notfound.lpickbackend.wiki.query.service;

import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiBookmark;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wiki.query.dto.response.ReviewResponse;
import com.notfound.lpickbackend.wiki.query.dto.response.WikiPageViewResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class WikiDomainQueryServiceTest {
    // 실제 DB 대상 테스트 메소드에만 별도 주석 기입합니다. ex. // JPA_TEST

    @Mock
    private WikiPageQueryService wikiPageQueryService;

    @Mock
    private PageRevisionQueryService pageRevisionQueryService;

    @Mock
    private WikiBookmarkQueryService wikiBookmarkQueryService;

    @Mock
    private WikiReviewQueryService wikiReviewQueryService;

    @InjectMocks
    private WikiDomainQueryService wikiDomainQueryService;

    @Test
    void getWikiPageViewTest() {
        /* given */
        // given이 너무 많아 엔티티 및 객체 구현 시 필요없는 필드 전부 미기입
        // id
        String dummyWikiId = "wiki-1";
        String dummyUserId = "user-1";
        String dummyRevisionId = "revision-1";
        String dummyWikiBookmark = "bookmark-1";

        // wiki
        WikiPage wikiPage = WikiPage.builder()
                .wikiId(dummyWikiId)
                .title("카프리썬/역사")
                .currentRevision("r3")
                .build();

        // pageRevision
        PageRevision pageRevision = PageRevision.builder()
                .revisionId(dummyRevisionId)
                .content("갑자기 카프리썬이 땡긴다..")
                .createdAt(Instant.now())
                .wiki(wikiPage)
                .build();

        // wikiBookmark
        WikiBookmark bookmark = WikiBookmark.builder()
                .wikiBookmarkId(dummyWikiBookmark)
                .build();

        // Page<ReviewResponse>
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        ReviewResponse r1 = ReviewResponse.builder()
                .createdAt(Instant.parse("2025-06-01T12:00:00Z"))
                .build();

        ReviewResponse r2 = ReviewResponse.builder()
                .createdAt(Instant.parse("2025-06-02T15:30:00Z"))
                .build();

        ReviewResponse r3 = ReviewResponse.builder()
                .createdAt(Instant.parse("2025-06-02T18:20:00Z"))
                .build();

        ReviewResponse r4 = ReviewResponse.builder()
                .createdAt(Instant.parse("2025-06-10T01:30:00Z"))
                .build();

        List<ReviewResponse> reviewList = Arrays.asList(r1, r2, r3, r4);

        Page<ReviewResponse> stubPage = new PageImpl<>(reviewList, pageable, reviewList.size());



        when(wikiPageQueryService.getWikiPageById(wikiPage.getWikiId()))
                .thenReturn(wikiPage);
        when(pageRevisionQueryService.findByPageRevision_revisionNumberAndWiki_wikiId(wikiPage.getCurrentRevision(), dummyWikiId))
                .thenReturn(pageRevision);

        // 북마크 존재하는 경우로 가정
        when(wikiBookmarkQueryService.findByWiki_WikiIdAndOauth_oauthId(dummyWikiId, dummyUserId))
                .thenReturn(Optional.of(bookmark));

        when(wikiReviewQueryService.getReviewResponseListInWiki(
                PageRequest.of(0, 10, Sort.by("createdAt").descending()), dummyWikiId))
                .thenReturn(stubPage);


        //when
        WikiPageViewResponse viewResponse = wikiDomainQueryService.getWikiPageView(dummyWikiId, dummyUserId);

        verify(wikiPageQueryService, times(1))
                .getWikiPageById(wikiPage.getWikiId());
        verify(pageRevisionQueryService, times(1))
                .findByPageRevision_revisionNumberAndWiki_wikiId(wikiPage.getCurrentRevision(), dummyWikiId);
        verify(wikiBookmarkQueryService, times(1))
                .findByWiki_WikiIdAndOauth_oauthId(dummyWikiId,dummyUserId);
        verify(wikiReviewQueryService, times(1))
                .getReviewResponseListInWiki(
                        PageRequest.of(0, 10,
                                Sort.by("createdAt").descending()),
                        dummyWikiId
                );

        WikiPageViewResponse expectedResponse =
                WikiPageViewResponse.builder()
                        .wikiId(wikiPage.getWikiId())
                        .title(wikiPage.getTitle())
                        .content(pageRevision.getContent())
                        .modifiedAt(pageRevision.getCreatedAt())
                        .bookmarkId(bookmark.getWikiBookmarkId())
                        .reviewList(stubPage)
                        .build();

        assertEquals(expectedResponse, viewResponse);

    }

    // JPA_TEST - 최신 값들이 잘 가져와지는지 확인
    @Test
    void getRecentlyModifiedWikiPageListTest() {

    }
}
