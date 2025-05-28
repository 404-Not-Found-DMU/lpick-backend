package com.notfound.lpickbackend.security.handler;

import com.notfound.lpickbackend.userinfo.command.application.service.OAuth2UserCommandService;
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

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    OAuth2UserCommandService userCommandService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        
        // OAuth2AuthenticationToken 으로 캐스팅
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        String oauthType = oauthToken.getAuthorizedClientRegistrationId();

        // OAuth2User 정보 꺼내기
        OAuth2User oauthUser = oauthToken.getPrincipal();
        // 사용자 정보 예: 카카오 id
        Object id = oauthUser.getAttribute("id");

        log.info("oauthType={}", oauthType);
        log.info("OAuth2User id = {}", id);

    }
}
