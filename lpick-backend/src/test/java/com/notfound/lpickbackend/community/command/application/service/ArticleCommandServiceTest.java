package com.notfound.lpickbackend.community.command.application.service;

import com.notfound.lpickbackend.community.command.application.dto.ArticleCreateRequestDTO;
import com.notfound.lpickbackend.community.command.application.dto.ArticleUpdateRequestDTO;
import com.notfound.lpickbackend.community.command.domain.Article;
import com.notfound.lpickbackend.community.command.domain.ArticleStatus;
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

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ArticleCommandServiceTest {

    @Mock
    private UserInfoQueryRepository userInfoQueryRepository;

    @Mock
    private ArticleCommandRepository articleCommandRepository;

    @InjectMocks
    private ArticleCommandService articleCommandService;

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
    void articleCreateTest() {
        // given
        ArticleCreateRequestDTO request = new ArticleCreateRequestDTO("제목", "내용");

        given(userInfoQueryRepository.findById("mock-oauth-id"))
                .willReturn(Optional.of(mockUser));

        // when
        articleCommandService.createArticle(request);

        // then
        then(articleCommandRepository).should()
                .save(argThat(article ->
                        article.getTitle().equals("제목")
                                && article.getContent().equals("내용")
                                && article.getOauth().equals(mockUser)
                ));
    }

    @Test
    void articleUpdateTest() {
        // given
        String articleId = "article-123";
        ArticleUpdateRequestDTO request = new ArticleUpdateRequestDTO("수정된 제목", "수정된 내용");

        Article existingArticle = Article.builder()
                .articleId(articleId)
                .title("기존 제목")
                .content("기존 내용")
                .oauth(mockUser) // 작성자 동일
                .isDel(ArticleStatus.N)
                .build();

        given(articleCommandRepository.findById(articleId))
                .willReturn(Optional.of(existingArticle));

        // when
        articleCommandService.updateArticle(articleId, request);

        // then
        then(articleCommandRepository).should()
                .save(argThat(article ->
                        article.getTitle().equals("수정된 제목")
                                && article.getContent().equals("수정된 내용")
                ));
    }

    @Test
    void articleDeleteTest() {
        // given
        String articleId = "article-456";

        Article existingArticle = Article.builder()
                .articleId(articleId)
                .title("삭제 대상 제목")
                .content("삭제 대상 내용")
                .oauth(mockUser) // 작성자 동일
                .isDel(ArticleStatus.N)
                .build();

        given(articleCommandRepository.findById(articleId))
                .willReturn(Optional.of(existingArticle));

        // when
        articleCommandService.deleteArticle(articleId);

        // then
        then(articleCommandRepository).should().delete(existingArticle);
    }
}