package com.notfound.lpickbackend.userinfo.query.service;

import com.notfound.lpickbackend.community.query.application.service.ArticleQueryService;
import com.notfound.lpickbackend.community.query.service.CommentQueryService;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserActivityResponse;
import com.notfound.lpickbackend.wiki.query.service.PageRevisionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDomainQueryService {

    private final ArticleQueryService articleQueryService;
    private final CommentQueryService commentQueryService;
    private final PageRevisionQueryService pageRevisionQueryService;

    public UserActivityResponse getUserActivityCount(String oauthId) {
        return UserActivityResponse.builder()
                .articleCount(articleQueryService.countArticleByOauthId(oauthId))
                .commentCount(commentQueryService.countCommentByOauthId(oauthId))
                .wikiEditCount(pageRevisionQueryService.countRevisionByOauthId(oauthId))
                .build();
    }
}
