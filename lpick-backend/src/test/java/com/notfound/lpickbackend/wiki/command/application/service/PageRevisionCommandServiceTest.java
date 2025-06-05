package com.notfound.lpickbackend.wiki.command.application.service;

import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import com.notfound.lpickbackend.temp.query.dto.response.UserIdNamePairResponse;
import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wiki.command.application.dto.request.PageRevisionRequest;
import com.notfound.lpickbackend.wiki.command.repository.PageRevisionCommandRepository;
import com.notfound.lpickbackend.wiki.query.dto.response.PageRevisionResponse;
import com.notfound.lpickbackend.wiki.query.service.PageRevisionQueryService;
import com.notfound.lpickbackend.wiki.query.service.WikiPageQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PageRevisionCommandServiceTest {

    @Mock
    private WikiPageCommandService wikiPageCommandService;

    @Mock
    private WikiPageQueryService wikiPageQueryService;

    @Mock
    private PageRevisionQueryService pageRevisionQueryService;

    @Mock
    private PageRevisionCommandRepository pageRevisionCommandRepository;

    @InjectMocks
    private PageRevisionCommandService pageRevisionCommandService;

    @Test
    void createNewRevision() {
        // given
        String dummyWikiId = "dummy-wiki-1";
        WikiPage mockWikiPage = mock(WikiPage.class);
        when(mockWikiPage.getWikiId()).thenReturn(dummyWikiId);


        String oauthId = "dummyUserId";
        String nickname = ("더미유저닉네임");
        UserInfo mockUserInfo = mock(UserInfo.class);
        when(mockUserInfo.getOauthId()).thenReturn(oauthId);
        when(mockUserInfo.getNickname()).thenReturn(nickname);

        when(wikiPageQueryService.getWikiPageById(dummyWikiId))
                .thenReturn(mockWikiPage);
        when(pageRevisionQueryService.wikicountByWiki_WikiId(dummyWikiId))
                .thenReturn(5L);
        // count 집계처리 기반 확인은 추후 더미데이터 쿼리 추가 구현 후 실제 DB와 연동되어 동작하게끔 테스트하도록 변경..


        Instant now = Instant.parse("2025-06-03T12:34:56Z");
        PageRevision savedRevision = PageRevision.builder()
                .revisionId("rev-999")
                .revisionNumber("r6") // 기존 리비전이 5개 있었다고 위에서 가정 + 1 연산
                .content("새로운 내용")
                .wiki(mockWikiPage)
                .userInfo(mockUserInfo)
                .createdAt(now)  // Builder에 createdAt을 직접 채워 주도록 가정
                .build();

        when(pageRevisionCommandRepository.save((any(PageRevision.class))))
                .thenReturn(savedRevision);

        // when
        PageRevisionRequest revisionRequest = PageRevisionRequest.builder()
                .content("새로운 내용")
                .build();

        PageRevisionResponse expectedRevisionResponse =
                PageRevisionResponse.builder()
                        .revisionId("rev-999")
                        .content("새로운 내용")
                        .createWho(
                                UserIdNamePairResponse.builder()
                                        .oauthId(mockUserInfo.getOauthId())
                                        .nickName(mockUserInfo.getNickname())
                                        .build()
                        )
                        .createdAt(now)
                        .build();
        // then
        PageRevisionResponse pageRevisionResponse =
                pageRevisionCommandService.createNewRevision(revisionRequest, dummyWikiId, mockUserInfo);


        verify(wikiPageCommandService, times(1))
                .updateWikiPageCurrentRevision(dummyWikiId, savedRevision.getRevisionNumber());


        assertEquals(expectedRevisionResponse, pageRevisionResponse);
    }
}