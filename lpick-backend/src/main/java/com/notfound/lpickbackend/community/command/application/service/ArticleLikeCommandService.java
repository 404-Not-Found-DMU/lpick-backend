package com.notfound.lpickbackend.community.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.community.command.domain.Article;
import com.notfound.lpickbackend.community.command.domain.ArticleLike;
import com.notfound.lpickbackend.community.command.repository.ArticleCommandRepository;
import com.notfound.lpickbackend.community.command.repository.ArticleLikeCommandRepository;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import com.notfound.lpickbackend.userinfo.query.repository.UserInfoQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleLikeCommandService {

    private final UserInfoQueryRepository userInfoQueryRepository;
    private final ArticleCommandRepository articleCommandRepository;
    private final ArticleLikeCommandRepository articleLikeCommandRepository;

    @Transactional
    public void createArticleLike(String articleId) {

        UserInfo userInfo = getUserInfo();
        Article article = getArticle(articleId);
        Optional<ArticleLike> like = getLike(userInfo, article);

        // 이미 삭제된 데이터에 대한 접근인지 확인
        if(article.checkIsDel()) {
            throw new CustomException(ErrorCode.NOT_FOUND_ARTICLE);
        }

        // 좋아요가 존재하지 않는다면 추가
        // 만약 존재하지 않는 게시글인 경우 getArticle()에서 예외처리 가능
        if(like.isEmpty()) {

            ArticleLike newLike = ArticleLike.builder()
                    .article(article)
                    .oauth(userInfo)
                    .build();

            articleLikeCommandRepository.save(newLike);
        } else { // 이미 좋아요 추가해놓은 상태
            throw new CustomException(ErrorCode.ALREADY_HAS_BOOKMARK);
        }
    }

    @Transactional
    public void deleteArticleLike(String articleId) {

        UserInfo userInfo = getUserInfo();
        Article article = getArticle(articleId);
        Optional<ArticleLike> like = getLike(userInfo, article);

        // 이미 삭제된 데이터에 대한 접근인지 확인
        if(article.checkIsDel()) {
            // 예외를 던지지 않는 이유는 삭제하려는 데이터가 이미 삭제된 경우에도 삭제된걸로 처리해야하기 때문.
            // GPT 피셜이라 고민이 좀더 필요할 것 같긴 합니다.
            return;
        }

        // 좋아요가 존재한다면 삭제
        // 만약 이미 존재하지 않더라도 삭제 처리와 다른게 없기때문에 예외처리 X
        like.ifPresent(articleLikeCommandRepository::delete);
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

    // 좋아요 정보 가져오는 메소드
    private Optional<ArticleLike> getLike(UserInfo userInfo, Article article) {

        return articleLikeCommandRepository.findByOauthAndArticle(userInfo, article);
    }
}
