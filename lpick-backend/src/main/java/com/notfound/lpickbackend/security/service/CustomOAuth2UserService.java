package com.notfound.lpickbackend.security.service;

import com.notfound.lpickbackend.AUTO_ENTITIES.Tier;
import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.security.details.CustomOAuthUser;
import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import com.notfound.lpickbackend.tier.query.repository.TierCommandRepository;
import com.notfound.lpickbackend.temp.command.repository.UserInfoCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserInfoCommandRepository userInfoCommandRepository;
    private final TierCommandRepository tierCommandRepository;
    private final String DEFAULT_TIER_ID = "1";

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = getOAuth2User(userRequest);

        String oAuthType = userRequest.getClientRegistration().getRegistrationId();

        String oAuthId = "";

        // 추후 Google 추가를 위한 switch case문
        switch (oAuthType) {
            case "kakao":
                oAuthId = oAuth2User.getAttribute("id").toString();
                break;
        }

        Optional<UserInfo> optionalUserInfo = userInfoCommandRepository.findByOauthId(oAuthId);

        UserInfo userInfo = null;

        if (optionalUserInfo.isPresent()) { // 유저가 있을 때
            userInfo = optionalUserInfo.get();
        } else { // 유저가 없을 때 (최초 로그인)
            // default
            Tier defaultTier = tierCommandRepository.findById(DEFAULT_TIER_ID).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_TIER)
            );

            // 기본값으로 회원가입 처리
            userInfo = UserInfo.builder()
                    .oauthId(oAuthType + oAuthId) // 전치사로 OAuthType 추가
                    .nickname("")
                    .profile("")
                    .point(0)
                    .stackPoint(0)
                    .about("")
                    .lpti("")
                    .tier(defaultTier)
                    .build();
            userInfoCommandRepository.save(userInfo);
        }

        return new CustomOAuthUser(userInfo);
    }

    public OAuth2UserDetails getUserDetails(String oAuthId) {

        UserInfo userInfo = userInfoCommandRepository.findByOauthId(oAuthId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        return new OAuth2UserDetails(userInfo);
    }

    // 테스트 시 Mock 처리할 수 있도록 protected로 분리
    protected OAuth2User getOAuth2User(OAuth2UserRequest userRequest) {
        return super.loadUser(userRequest);
    }
}
