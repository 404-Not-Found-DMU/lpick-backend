package com.notfound.lpickbackend.security.handler;

import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.security.util.CustomOAuthUser;
import com.notfound.lpickbackend.userinfo.command.application.service.OAuth2UserCommandService;
import com.notfound.lpickbackend.userinfo.command.repository.UserInfoCommandRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    UserInfoCommandRepository userInfoCommandRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("oauth2 success");

        CustomOAuthUser oAuthUser = (CustomOAuthUser) authentication.getPrincipal();

        String oAuthId = oAuthUser.getName(); // CustomOAuthUser의 oAuthID return받음

        UserInfo userInfo = userInfoCommandRepository.findById(oAuthId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        Map<String, Object> claims = new HashMap<>();
        claims.put("oAuthId", userInfo.getOauthType());
        claims.put("oAuthType", userInfo.getOauthType());
        claims.put("Tier", userInfo.getTier());
    }
}
