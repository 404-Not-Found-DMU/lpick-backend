package com.notfound.lpickbackend.security.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.security.details.CustomOAuthUser;
import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import com.notfound.lpickbackend.tier.query.repository.TierCommandRepository;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.Tier;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserSetting;
import com.notfound.lpickbackend.userinfo.command.repository.UserInfoCommandRepository;
import com.notfound.lpickbackend.userinfo.command.repository.UserSettingCommandRepository;
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
    private final UserSettingCommandRepository userSettingCommandRepository;
    private final TierCommandRepository tierCommandRepository;
    private final String DEFAULT_TIER_ID = "1";

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("load user start");

        OAuth2User oAuth2User = getOAuth2User(userRequest);

        String oAuthType = userRequest.getClientRegistration().getRegistrationId();

        String oAuthId = "";

        // 추후 Google 추가를 위한 switch case문
        switch (oAuthType) {
            case "kakao":
                oAuthId = oAuth2User.getAttribute("id").toString();
                break;
        }

        Optional<UserInfo> optionalUserInfo = userInfoCommandRepository.findByOauthId(oAuthType + oAuthId);


        if (optionalUserInfo.isPresent()) { // 유저가 있을 때
            log.warn("user already registration");
            UserInfo userInfo = optionalUserInfo.get();
            return new CustomOAuthUser(userInfo);
        } else { // 유저가 없을 때 (최초 로그인)
            // default
            Tier defaultTier = tierCommandRepository.findById(DEFAULT_TIER_ID).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_TIER)
            );

            // 기본값으로 회원가입 처리
            UserInfo userInfo = UserInfo.builder()
                    .oauthId(oAuthType + oAuthId) // 전치사로 OAuthType 추가
                    .nickname("")
                    .profile("")
                    .point(0)
                    .stackPoint(0)
                    .about("")
                    .lpti("")
                    .tier(defaultTier)
                    .build();
            UserInfo createdUserInfo = userInfoCommandRepository.save(userInfo);
            log.info("user save success");
            log.info("user oauth Id : {}", userInfo.getOauthId());

            // UserSetting 이원화에 따라 사용자 회원가입 시 UserSetting 기본 엔티티 구현 및 기본값 저장 위한 코드 추가.
            UserSetting defaultUserSetting = new UserSetting();
            defaultUserSetting.setToDefault(createdUserInfo); // 디폴트 설정(모두 true, LIGHT 테마)
            userSettingCommandRepository.save(defaultUserSetting);
            log.info("user setting save success");

            return new CustomOAuthUser(userInfo);
        }
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
