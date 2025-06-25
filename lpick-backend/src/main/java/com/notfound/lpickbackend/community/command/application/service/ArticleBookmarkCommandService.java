package com.notfound.lpickbackend.community.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.community.command.domain.Article;
import com.notfound.lpickbackend.community.command.domain.ArticleBookmark;
import com.notfound.lpickbackend.community.command.repository.ArticleBookmarkCommandRepository;
import com.notfound.lpickbackend.community.command.repository.ArticleCommandRepository;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import com.notfound.lpickbackend.userinfo.query.repository.UserInfoQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleBookmarkCommandService {

    private final UserInfoQueryRepository userInfoQueryRepository;
    private final ArticleCommandRepository articleCommandRepository;
    private final ArticleBookmarkCommandRepository articleBookmarkCommandRepository;

    @Transactional
    public void createArticleBookmark(String articleId) {

        UserInfo userInfo = getUserInfo();
        Article article = getArticle(articleId);
        Optional<ArticleBookmark> bookmark = getBookmark(userInfo, article);

        // 이미 삭제된 데이터에 대한 접근인지 확인
        if(article.checkIsDel()) {
            throw new CustomException(ErrorCode.NOT_FOUND_ARTICLE);
        }

        // 북마크가 존재하지 않는다면 추가
        // 만약 존재하지 않는 게시글인 경우 getArticle()에서 예외처리 가능
        if(bookmark.isEmpty()) {

            ArticleBookmark newBookmark = ArticleBookmark.builder()
                    .article(article)
                    .oauth(userInfo)
                    .build();

            articleBookmarkCommandRepository.save(newBookmark);
        } else {
            throw new CustomException(ErrorCode.ALREADY_HAS_BOOKMARK);
        }
    }

    @Transactional
    public void deleteArticleBookmark(String articleId) {

        UserInfo userInfo = getUserInfo();
        Article article = getArticle(articleId);
        Optional<ArticleBookmark> bookmark = getBookmark(userInfo, article);

        // 이미 삭제된 데이터에 대한 접근인지 확인
        if(article.checkIsDel()) {
            return;
        }

        // 북마크가 존재한다면 삭제
        // 만약 이미 존재하지 않더라도 삭제 처리와 다른게 없기때문에 예외처리 X
        bookmark.ifPresent(articleBookmarkCommandRepository::delete);
    }

    // 서비스 내부에서 사용할 UserInfo 찾는 메소드
    private UserInfo getUserInfo() {

        return userInfoQueryRepository.findById(UserInfoUtil.getOAuthId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );
    }

    // 서비스 내부에서 사용할 Article 찾는 메소드
    private Article getArticle(String articleId) {

        return articleCommandRepository.findById(articleId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE)
        );
    }

    // 북마크 정보 가져오는 메소드
    private Optional<ArticleBookmark> getBookmark(UserInfo userInfo, Article article) {

        return articleBookmarkCommandRepository.findByOauthAndArticle(userInfo, article);
    }
}
