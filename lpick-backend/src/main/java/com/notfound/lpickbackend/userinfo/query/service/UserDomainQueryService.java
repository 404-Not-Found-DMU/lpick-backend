package com.notfound.lpickbackend.userinfo.query.service;

import com.notfound.lpickbackend.common._wrapper.BlindableResponse;
import com.notfound.lpickbackend.community.query.application.service.ArticleQueryService;
import com.notfound.lpickbackend.community.query.service.CommentQueryService;
import com.notfound.lpickbackend.debate.query.service.DebateChaQueryService;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserSetting;
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
    private final UserSettingQueryService userSettingQueryService;
    private final DebateChaQueryService debateChaQueryService;

    public BlindableResponse<UserActivityResponse> getUserActivityCountByOauthId(String oauthId) {

        UserSetting userSetting = userSettingQueryService.findById(oauthId);
        if(!userSetting.getMyPagePrivacySetting().isUserAllowViewActCount()) // 마이페이지 주인이 활동 카운트 공유 여부를를 false로 설정한 경우
            return BlindableResponse.of(
                    userSetting.getMyPagePrivacySetting().isUserAllowViewActCount(),
                            UserActivityResponse.builder()
                                    .wikiEditCount(null)
                                    .commentCount(null)
                                    .articleCount(null)
                                    .debateChatCount(null)
                                    .build()
            );

        return BlindableResponse.of(
                userSetting.getMyPagePrivacySetting().isUserAllowViewActCount(),
                getUserActivityCount(oauthId)
        );
    }

    public UserActivityResponse getUserActivityCount(String oauthId) {
        return UserActivityResponse.builder()
                .articleCount(articleQueryService.countArticleByOauthId(oauthId))
                .commentCount(commentQueryService.countCommentByOauthId(oauthId))
                .wikiEditCount(pageRevisionQueryService.countRevisionByOauthId(oauthId))
                .debateChatCount(debateChaQueryService.countByOauthId(oauthId))
                .build();
    }
}
