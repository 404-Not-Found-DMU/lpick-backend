package com.notfound.lpickbackend.wiki.query.service;

import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiBookmark;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wiki.query.dto.response.ReviewResponse;
import com.notfound.lpickbackend.wiki.query.dto.response.WikiPageTitleResponse;
import com.notfound.lpickbackend.wiki.query.dto.response.WikiPageViewResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private Instant fixedNow;

    @BeforeEach
    void setUp() {
        // 테스트 시점의 "현재 시간"을 고정해 둡니다.
        // TimeAgoUtil.toTimeAgo() 내부에서 Instant.now()를 직접 쓰고 있다면,
        // 테스트마다 예상 결과가 달라질 수 있으므로 아래처럼 미리 정해둡니다.
        fixedNow = Instant.parse("2025-06-10T12:00:00Z");
    }

    @Test
    @DisplayName("getWikiPageView : 사용자가 위키 문서 페이지에 진입했을 때 페이지에서 제공해야할 정보들을 지닌 WikiPageViewResponse가 정상적으로 만들어지는지 시나리오와 결과를 검증한다.")
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


    @Test
    @DisplayName("getRecentlyModifiedWikiPageList: 최신 PageRevision을 받아와서 DTO로 변환. TimeAgoUtil 정상동작 테스트 포함")
    void getRecentlyModifiedWikiPageList_shouldReturnCorrectTitleResponses() {
        int pageAmount = 3;

        // 1) 샘플 WikiPage 객체 생성
        WikiPage wiki1 = WikiPage.builder()
                .wikiId("wiki-1")
                .title("테스트 위키1")
                .currentRevision("r2")
                .build();

        WikiPage wiki2 = WikiPage.builder()
                .wikiId("wiki-2")
                .title("테스트 위키2")
                .currentRevision("r1")
                .build();

        WikiPage wiki3 = WikiPage.builder()
                .wikiId("wiki-3")
                .title("테스트 위키3")
                .currentRevision("r1")
                .build();

        // 2) 샘플 PageRevision 객체. Instant.minusSeconds(...)를 써서 시간 차이를 정의
        PageRevision pr1 = PageRevision.builder()
                .revisionId("rev-1")
                .revisionNumber("r1")
                .content("내용 1")
                // 55일 전 =  55*24*60*60초 전
                .createdAt(fixedNow.minusSeconds(55 * 24 * 60 * 60))
                .wiki(wiki1)
                .userInfo(null)
                .build();

        PageRevision pr2 = PageRevision.builder()
                .revisionId("rev-2")
                .revisionNumber("r2")
                .content("내용 2")
                // 2시간 전 = 2*60*60초 전
                .createdAt(fixedNow.minusSeconds(2 * 60 * 60))
                .wiki(wiki2)
                .userInfo(null)
                .build();

        PageRevision pr3 = PageRevision.builder()
                .revisionId("rev-3")
                .revisionNumber("r3")
                .content("내용 3")
                // 5분 전 = 5*60초 전
                .createdAt(fixedNow.minusSeconds(5 * 60))
                .wiki(wiki3)
                .userInfo(null)
                .build();

        // 최신순: pr3(5분 전), pr2(2시간 전), pr1(55일 전)
        List<PageRevision> mockedList = Arrays.asList(pr3, pr2, pr1);
        Page<PageRevision> mockedPage = new PageImpl<>(mockedList, PageRequest.of(0, pageAmount), mockedList.size());

        // 3) Mockito stub: getLatestRevisionPerWiki 호출 시 mockedPage 반환
        when(pageRevisionQueryService.getLatestRevisionPerWiki(PageRequest.of(0, pageAmount)))
                .thenReturn(mockedPage);

        // --- 실제 테스트 대상 호출 ---
        // fixedNow 로 대체하여 전달
        List<WikiPageTitleResponse> responses = wikiDomainQueryService.getRecentlyModifiedWikiPageList(pageAmount, fixedNow);

        // --- 검증 ---
        assertThat(responses).hasSize(3);

        // 0번째: pr3 → wiki3, "5분 전" 문자열 포함
        WikiPageTitleResponse r0 = responses.get(0);
        assertThat(r0.getWikiId()).isEqualTo("wiki-3");
        assertThat(r0.getTitle()).isEqualTo("테스트 위키3");
        assertThat(r0.getModifiedBefore()).contains("5분");

        // 1번째: pr2 → wiki2, "2시간 전" 문자열 포함
        WikiPageTitleResponse r1resp = responses.get(1);
        assertThat(r1resp.getWikiId()).isEqualTo("wiki-2");
        assertThat(r1resp.getTitle()).isEqualTo("테스트 위키2");
        assertThat(r1resp.getModifiedBefore()).contains("2시간");

        // 2번째: pr1 → wiki1, "55일 전" 문자열 포함
        WikiPageTitleResponse r2resp = responses.get(2);
        assertThat(r2resp.getWikiId()).isEqualTo("wiki-1");
        assertThat(r2resp.getTitle()).isEqualTo("테스트 위키1");
        assertThat(r2resp.getModifiedBefore()).contains("55일");
    }
}
