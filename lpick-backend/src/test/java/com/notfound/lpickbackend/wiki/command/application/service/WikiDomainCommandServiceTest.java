package com.notfound.lpickbackend.wiki.command.application.service;

import com.notfound.lpickbackend.AUTO_ENTITIES.Album;
import com.notfound.lpickbackend.AUTO_ENTITIES.Artist;
import com.notfound.lpickbackend.AUTO_ENTITIES.Gear;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.userinfo.query.service.UserInfoQueryService;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wiki.command.application.dto.request.WikiPageCreateRequestDTO;
import com.notfound.lpickbackend.wiki.query.service.PageRevisionQueryService;
import com.notfound.lpickbackend.wiki.query.service.WikiPageQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WikiDomainCommandServiceTest {

    @Mock
    private WikiPageCommandService wikiPageCommandService;

    @Mock
    private WikiPageQueryService wikiPageQueryService;

    @Mock
    private PageRevisionCommandService pageRevisionCommandService;

    @Mock
    private PageRevisionQueryService pageRevisionQueryService;

    @Mock
    private WikiBookmarkCommandService wikiBookmarkCommandService;

    @Mock
    private UserInfoQueryService userInfoQueryService;

    @InjectMocks
    private WikiDomainCommandService wikiDomainCommandService;



    private WikiPageCreateRequestDTO requestDTO;

    private WikiPageCreateRequestDTO expectedRequestDTO;

    private WikiPage dummyWiki = new WikiPage();
    private String dummyWikiId = "dummyId";

    @BeforeEach
    void setUp() {
        requestDTO = WikiPageCreateRequestDTO.builder()
                .title("테스트 문서")
                .content("테스트 내용입니다.")
                .userId("user-123")
                .build();

        expectedRequestDTO = WikiPageCreateRequestDTO.builder()
                .title("에러 테스트")
                .content("내용")
                .userId("not-exist-user")
                .build();

        dummyWiki.setWikiId("dummyId");
    }

    @Test // 각 서비스 메소드가 정상적으로 호출되는지 확인
    void createWikiPageAndRevisionCreateTest() {
        // given
        String generatedWikiId = "wiki-uuid-001";
        UserInfo mockUserInfo = mock(UserInfo.class);

        when(wikiPageCommandService.createWikiPage("테스트 문서"))
                .thenReturn(generatedWikiId);
        when(userInfoQueryService.getUserInfoById("user-123"))
                .thenReturn(mockUserInfo);

        // when
        wikiDomainCommandService.createWikiPageAndRevision(requestDTO);

        // then
        verify(wikiPageCommandService, times(1)).createWikiPage("테스트 문서");
        verify(userInfoQueryService, times(1)).getUserInfoById("user-123");
        verify(pageRevisionCommandService, times(1)).createNewRevision(
                argThat(req -> req.getContent().equals("테스트 내용입니다.")
                ),
                eq(generatedWikiId),
                eq(mockUserInfo)
        );
    }

    @Test // 유저 확인 에러 테스트
    void notFoundUserInfoError() {
        // given
        when(wikiPageCommandService.createWikiPage(anyString()))
                .thenReturn("wiki-001");
        when(userInfoQueryService.getUserInfoById("not-exist-user"))
                .thenThrow(new CustomException(ErrorCode.AUTHENTICATION_FAILED));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            wikiDomainCommandService.createWikiPageAndRevision(expectedRequestDTO);
        });

        assertEquals(ErrorCode.AUTHENTICATION_FAILED, exception.getErrorCode());
    }


    @Test
    void revertWikiPageAndRevision() {
    }

    @Test
    void hardDeleteWikiPageTestWhenArtist_Album_Gear() {
        // given : artist, album, gear가 null이 아닌 경우
        // 주의사항 : wikipage는 실제로는 artist 또는 album 또는 gear 중 하나와만 연관관계를 지니도록 설계됨.
        // 즉, 실제로는 artist 만, album 만, gear 만 관계를 지니나 테스트 확인 위해 세가지 모두 설정
        Artist artist = mock(Artist.class);
        artist.setArtistId("artist-1");
        artist.setWiki(dummyWiki);

        Album album = mock(Album.class);
        album.setAlbumId("album-1");
        album.setWiki(dummyWiki);

        Gear gear = mock(Gear.class);
        gear.setEqId("album-1");
        gear.setWiki(dummyWiki);

        dummyWiki.setArtist(artist);
        dummyWiki.setAlbum(album);
        dummyWiki.setGear(gear);

        // hardDeleteWikiPage에서 return 값과 함께 사용되는 메소드들의 return 값 설정
        when(wikiPageQueryService.getWikiPageById(dummyWikiId))
                .thenReturn(dummyWiki);

        //when : hardDeleteWikiPage 설정 한 경우
        wikiDomainCommandService.hardDeleteWikiPage(dummyWikiId);


        assertNull(artist.getWiki(), "Artist가 Null인지 검증");

        // 1회만 불러지는지 검증
        // bulk delete 잘 호출하는지 검증
        verify(pageRevisionCommandService, times(1))
                .deleteRevisionDataByWiki_WikiId(dummyWikiId);
        verify(wikiBookmarkCommandService, times(1))
                .deleteAllBookmarkDataByWiki_WikiId(dummyWikiId);

        // 최종 삭제 잘 호출하는지 검증
        verify(wikiPageCommandService, times(1))
                .deleteWikiPageById(dummyWikiId);
    }
}