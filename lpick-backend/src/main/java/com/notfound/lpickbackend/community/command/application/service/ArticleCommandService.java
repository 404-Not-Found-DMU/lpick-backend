package com.notfound.lpickbackend.community.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.community.command.application.domain.Article;
import com.notfound.lpickbackend.community.command.application.domain.ArticleStatus;
import com.notfound.lpickbackend.community.command.application.dto.ArticleRequest;
import com.notfound.lpickbackend.community.command.repository.ArticleCommandRepository;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import com.notfound.lpickbackend.userinfo.query.repository.UserInfoQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleCommandService {

    private final UserInfoQueryRepository userInfoQueryRepository;
    private final ArticleCommandRepository articleCommandRepository;

    /* 이미지 처리 로직은 추후 프론트엔드와 협의 후 진행
    *  생각중인 로직은(게시글에 에디터 사용한다는 가정)
    *  1. 프론트에서 이미지 저장 요청(AWS S3)
    *  2. 백엔드에서 이미지 S3에 저장 후 이미지 url 리턴
    *  3. 리턴 받은 이미지 url로 img태그 채워서 게시글 저장 요청 ex) <img src="asdqw@#!@ADS...">
    *  4. img 태그가 포함된 content 자체를 DB에 저장
    * */
    @Transactional
    public void createArticle(ArticleRequest articleRequest) {

        UserInfo userInfo = getUserInfo();

        Article newArticle = Article.builder()
                .title(articleRequest.getTitle())
                .content(articleRequest.getContent())
                .oauth(userInfo)
                .isDel(ArticleStatus.N)
                .build();

        articleCommandRepository.save(newArticle);
    }

    // 서비스 내부에서 사용할 UserInfo 찾는 메소드
    private UserInfo getUserInfo() {

        return userInfoQueryRepository.findById(UserInfoUtil.getOAuthId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );
    }
}
