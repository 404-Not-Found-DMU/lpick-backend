package com.notfound.lpickbackend.community.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.community.command.application.domain.Article;
import com.notfound.lpickbackend.community.command.application.domain.ArticleBookmark;
import com.notfound.lpickbackend.community.command.application.domain.ArticleStatus;
import com.notfound.lpickbackend.community.command.application.dto.ArticleCreateRequest;
import com.notfound.lpickbackend.community.command.application.dto.ArticleUpdateRequest;
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
public class ArticleCommandService {

    private final UserInfoQueryRepository userInfoQueryRepository;
    private final ArticleCommandRepository articleCommandRepository;
    private final ArticleBookmarkCommandRepository articleBookmarkCommandRepository;

    /* 이미지 처리 로직은 추후 프론트엔드와 협의 후 진행
    *  생각중인 로직은(게시글에 에디터 사용한다는 가정)
    *  1. 프론트에서 이미지 저장 요청(AWS S3)
    *  2. 백엔드에서 이미지 S3에 저장 후 이미지 url 리턴
    *  3. 리턴 받은 이미지 url로 img태그 채워서 게시글 저장 요청 ex) <img src="asdqw@#!@ADS...">
    *  4. img 태그가 포함된 content 자체를 DB에 저장
    * */
    @Transactional
    public void createArticle(ArticleCreateRequest articleCreateRequest) {

        UserInfo userInfo = getUserInfo();

        Article newArticle = Article.builder()
                .title(articleCreateRequest.getTitle())
                .content(articleCreateRequest.getContent())
                .oauth(userInfo)
                .isDel(ArticleStatus.N)
                .build();

        articleCommandRepository.save(newArticle);
    }

    @Transactional
    public void updateArticle(String articleId, ArticleUpdateRequest articleUpdateRequest) {

        Article article = getArticle(articleId);

        // 이미 삭제된 데이터에 대한 접근인지 확인
        if(article.checkIsDel()) {
            throw new CustomException(ErrorCode.NOT_FOUND_ARTICLE);
        }

        // 접근 가능한 유저인지 확인
        if(checkUserInfo(article)) {
            throw new CustomException(ErrorCode.FORBIDDEN_RESOURCE_ACCESS);
        }

        article.updateContent(
                articleUpdateRequest.getTitle(),
                articleUpdateRequest.getContent()
        );

        articleCommandRepository.save(article);
    }

    @Transactional
    public void deleteArticle(String articleId) {

        Article article = getArticle(articleId);

        // 이미 삭제된 데이터에 대한 접근인지 확인
        if(article.checkIsDel()) {
            throw new CustomException(ErrorCode.NOT_FOUND_ARTICLE);
        }

        // 접근 가능한 유저인지 확인
        if(checkUserInfo(article)) {
            throw new CustomException(ErrorCode.FORBIDDEN_RESOURCE_ACCESS);
        }

        articleCommandRepository.delete(article);
    }

    @Transactional
    public void createBookmark(String articleId) {

        UserInfo userInfo = getUserInfo();
        Article article = getArticle(articleId);
        Optional<ArticleBookmark> bookmark = getBookmark(userInfo, article);

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
    public void deleteBookmark(String articleId) {

        UserInfo userInfo = getUserInfo();
        Article article = getArticle(articleId);
        Optional<ArticleBookmark> bookmark = getBookmark(userInfo, article);

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

    private Optional<ArticleBookmark> getBookmark(UserInfo userInfo, Article article) {

        return articleBookmarkCommandRepository.findByOauthAndArticle(userInfo, article);
    }

    private boolean checkUserInfo(Article article) {

        return !article.getOauth().getOauthId().equals(UserInfoUtil.getOAuthId());
    }
}
