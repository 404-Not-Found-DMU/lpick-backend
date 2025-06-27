package com.notfound.lpickbackend.community.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.community.command.domain.Article;
import com.notfound.lpickbackend.community.command.domain.ArticleBookmark;
import com.notfound.lpickbackend.community.command.domain.ArticleStatus;
import com.notfound.lpickbackend.community.command.repository.ArticleBookmarkCommandRepository;
import com.notfound.lpickbackend.community.command.repository.ArticleCommandRepository;
import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import com.notfound.lpickbackend.userinfo.command.application.domain.Tier;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import com.notfound.lpickbackend.userinfo.query.repository.UserInfoQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ArticleBookmarkCommandServiceTest {

    @Mock
    private UserInfoQueryRepository userInfoQueryRepository;

    @Mock
    private ArticleCommandRepository articleCommandRepository;

    @Mock
    private ArticleBookmarkCommandRepository articleBookmarkCommandRepository;

    @InjectMocks
    private ArticleBookmarkCommandService articleBookmarkCommandService;

    private UserInfo mockUser;
    private Article mockArticle;

    @BeforeEach
    void setUp() {

        Tier tier = Tier.builder().
                tierId("mockId").
                name("mockName").
                pointScope(0).
                build();

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

        mockArticle = Article.builder()
                .articleId("article-1")
                .oauth(mockUser)
                .isDel(ArticleStatus.N)
                .build();
    }

    @Test
    void bookmarkCreateTest() {
        // given
        given(userInfoQueryRepository.findById("mock-oauth-id")).willReturn(Optional.of(mockUser));
        given(articleCommandRepository.findById("article-1")).willReturn(Optional.of(mockArticle));
        given(articleBookmarkCommandRepository.findByOauthAndArticle(mockUser, mockArticle)).willReturn(Optional.empty());

        // when
        articleBookmarkCommandService.createArticleBookmark("article-1");

        // then
        then(articleBookmarkCommandRepository).should()
                .save(argThat(bookmark ->
                        bookmark.getArticle().equals(mockArticle)
                                && bookmark.getOauth().equals(mockUser)
                ));
    }

    // 북마크 생성 예외 처리 테스트
    @Test
    void bookmarkAlreadyExceptionTest() {
        // given
        ArticleBookmark existingBookmark = ArticleBookmark.builder()
                .oauth(mockUser)
                .article(mockArticle)
                .build();

        given(userInfoQueryRepository.findById("mock-oauth-id")).willReturn(Optional.of(mockUser));
        given(articleCommandRepository.findById("article-1")).willReturn(Optional.of(mockArticle));
        given(articleBookmarkCommandRepository.findByOauthAndArticle(mockUser, mockArticle)).willReturn(Optional.of(existingBookmark));

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> articleBookmarkCommandService.createArticleBookmark("article-1"));

        assertEquals(ErrorCode.ALREADY_HAS_BOOKMARK, exception.getErrorCode());
    }

    @Test
    void bookmarkDeleteTest() {
        // given
        ArticleBookmark existingBookmark = ArticleBookmark.builder()
                .oauth(mockUser)
                .article(mockArticle)
                .build();

        given(userInfoQueryRepository.findById("mock-oauth-id")).willReturn(Optional.of(mockUser));
        given(articleCommandRepository.findById("article-1")).willReturn(Optional.of(mockArticle));
        given(articleBookmarkCommandRepository.findByOauthAndArticle(mockUser, mockArticle)).willReturn(Optional.of(existingBookmark));

        // when
        articleBookmarkCommandService.deleteArticleBookmark("article-1");

        // then
        then(articleBookmarkCommandRepository).should().delete(existingBookmark);
    }
}