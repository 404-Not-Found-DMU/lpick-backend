package com.notfound.lpickbackend.wikipage.command.application.service;

import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.userInfo.query.service.UserInfoQueryService;
import com.notfound.lpickbackend.wiki.command.application.service.PageRevisionCommandService;
import com.notfound.lpickbackend.wikipage.command.application.dto.WikiPageCreateRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WikiPageAndRevisionCommandServiceTest {

    @Mock
    private WikiPageCommandService wikiPageCommandService;

    @Mock
    private PageRevisionCommandService pageRevisionCommandService;

    @Mock
    private UserInfoQueryService userInfoQueryService;

    @InjectMocks
    private WikiPageAndRevisionCommandService wikiPageAndRevisionCommandService;

    private WikiPageCreateRequestDTO requestDTO;

    private WikiPageCreateRequestDTO expectedRequestDTO;

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
        wikiPageAndRevisionCommandService.createWikiPageAndRevision(requestDTO);

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
            wikiPageAndRevisionCommandService.createWikiPageAndRevision(expectedRequestDTO);
        });

        assertEquals(ErrorCode.AUTHENTICATION_FAILED, exception.getErrorCode());
    }

}