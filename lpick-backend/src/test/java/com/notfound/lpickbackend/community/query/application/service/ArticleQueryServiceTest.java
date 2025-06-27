package com.notfound.lpickbackend.community.query.application.service;


import com.notfound.lpickbackend.community.query.application.dto.ArticleDetailResponseDTO;
import com.notfound.lpickbackend.community.query.application.dto.ArticleListResponseDTO;
import com.notfound.lpickbackend.community.query.repository.ArticleBookmarkQueryRepository;
import com.notfound.lpickbackend.community.query.repository.ArticleLikeQueryRepository;
import com.notfound.lpickbackend.community.query.repository.ArticleQueryRepository;
import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import com.notfound.lpickbackend.userinfo.command.application.domain.Tier;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ArticleQueryServiceTest {
    @Mock
    private ArticleQueryRepository articleQueryRepository;

    @Mock
    private ArticleBookmarkQueryRepository articleBookmarkQueryRepository;

    @Mock
    private ArticleLikeQueryRepository articleLikeQueryRepository;

    @InjectMocks
    private ArticleQueryService articleQueryService;

    private UserInfo mockUser;

    @BeforeEach
    void setUp() {

        Tier tier = Tier.builder().
                tierId("mockId").
                name("mockName").
                pointScope(0).
                build();

        // SecurityContext에 mock 사용자 등록
        mockUser = UserInfo.builder()
                .oauthId("mock-oauth-id") // 전치사로 OAuthType 추가
                .nickname("")
                .profile("")
                .point(0)
                .stackPoint(0)
                .about("")
                .lpti("")
                .tier(tier)
                .build();

        OAuth2UserDetails principal = new OAuth2UserDetails(mockUser);

        var authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void allArticleReadTest() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<ArticleListResponseDTO> articles = List.of(
                new ArticleListResponseDTO("id1", "제목1", 3L, 1L, 0L, "mock-oauth-id")
        );
        given(articleQueryRepository.findAllWithLikeAndCommentAndBookmarkCount(pageable))
                .willReturn(new PageImpl<>(articles));

        // when
        List<ArticleListResponseDTO> result = articleQueryService.readAllArticleList(pageable).getContent();

        // then
        assertEquals(1, result.size());
        assertEquals("제목1", result.get(0).getTitle());
    }

    @Test
    void myArticleReadTest() {
        // given
        Pageable pageable = PageRequest.of(0, 5);
        List<ArticleListResponseDTO> myList = List.of(
                new ArticleListResponseDTO("id1", "내글", 0L, 0L, 0L, "mock-oauth-id")
        );
        given(articleQueryRepository.findMyWithLikeAndCommentAndBookmarkCount("mock-oauth-id", pageable))
                .willReturn(new PageImpl<>(myList));

        // when
        List<ArticleListResponseDTO> result = articleQueryService.readMyArticleList(pageable).getContent();

        // then
        assertEquals(1, result.size());
        assertEquals("내글", result.get(0).getTitle());
    }

    @Test
    void myLikedArticleReadTest() {
        // given
        Pageable pageable = PageRequest.of(0, 5);
        List<ArticleListResponseDTO> likedList = List.of(
                new ArticleListResponseDTO("id1", "좋아요한 글", 1L, 0L, 0L, "mock-oauth-id")
        );
        given(articleQueryRepository.findMyLikedWithLikeAndCommentAndBookmarkCount("mock-oauth-id", pageable))
                .willReturn(new PageImpl<>(likedList));

        // when
        List<ArticleListResponseDTO> result = articleQueryService.readMyLikedArticleList(pageable).getContent();

        // then
        assertEquals(1, result.size());
        assertEquals("좋아요한 글", result.get(0).getTitle());
    }
}