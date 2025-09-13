package com.notfound.lpickbackend.community.query.application.service;


import com.notfound.lpickbackend.community.query.dto.ArticleListResponse;
import com.notfound.lpickbackend.community.query.repository.ArticleBookmarkQueryRepository;
import com.notfound.lpickbackend.community.query.repository.ArticleLikeQueryRepository;
import com.notfound.lpickbackend.community.query.repository.ArticleQueryRepository;
import com.notfound.lpickbackend.community.query.service.ArticleQueryService;
import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.Tier;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ArticleQueryServiceTest {
    private static final String OAUTH_ID = "mock-oauth-id";
    private static final Instant FIXED_NOW = Instant.parse("2025-01-01T00:00:00Z");

    @Mock private ArticleQueryRepository articleQueryRepository;
    @Mock private ArticleBookmarkQueryRepository articleBookmarkQueryRepository;
    @Mock private ArticleLikeQueryRepository articleLikeQueryRepository;

    @InjectMocks
    private ArticleQueryService articleQueryService;

    @BeforeEach
    void setUp() {
        setAuthentication(OAUTH_ID);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void allArticleReadTest() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<ArticleListResponse> articles = List.of(
                new ArticleListResponse("id1", "제목1", FIXED_NOW, FIXED_NOW, 3L, 1L, 0L, OAUTH_ID)
        );
        given(articleQueryRepository.findAllWithLikeAndCommentAndBookmarkCount(pageable))
                .willReturn(new PageImpl<>(articles));

        // when
        var result = articleQueryService.readAllArticleList(pageable).getContent();

        // then
        assertEquals(1, result.size());
        assertEquals("제목1", result.get(0).getTitle());
    }

    @Test
    void myArticleReadTest() {
        // given
        Pageable pageable = PageRequest.of(0, 5);
        List<ArticleListResponse> myList = List.of(
                new ArticleListResponse("id1", "내글", FIXED_NOW, FIXED_NOW, 0L, 0L, 0L, OAUTH_ID)
        );
        given(articleQueryRepository.findMyWithLikeAndCommentAndBookmarkCount(OAUTH_ID, pageable))
                .willReturn(new PageImpl<>(myList));

        // when
        var result = articleQueryService.readMyArticleList(pageable).getContent();

        // then
        assertEquals(1, result.size());
        assertEquals("내글", result.get(0).getTitle());
    }

    @Test
    void myLikedArticleReadTest() {
        // given
        Pageable pageable = PageRequest.of(0, 5);
        List<ArticleListResponse> likedList = List.of(
                new ArticleListResponse("id1", "좋아요한 글", FIXED_NOW, FIXED_NOW, 1L, 0L, 0L, OAUTH_ID)
        );
        given(articleQueryRepository.findMyLikedWithLikeAndCommentAndBookmarkCount(OAUTH_ID, pageable))
                .willReturn(new PageImpl<>(likedList));

        // when
        var result = articleQueryService.readMyLikedArticleList(pageable).getContent();

        // then
        assertEquals(1, result.size());
        assertEquals("좋아요한 글", result.get(0).getTitle());
    }

    // ---------- helpers ----------
    private static void setAuthentication(String oauthId) {
        Tier tier = Tier.builder()
                .tierId("mockId")
                .name("mockName")
                .pointScope(0)
                .build();

        UserInfo user = UserInfo.builder()
                .oauthId(oauthId)
                .nickname("")
                .profile("")
                .point(0)
                .stackPoint(0)
                .about("")
                .lpti("")
                .tier(tier)
                .build();

        var principal = new OAuth2UserDetails(user);
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}