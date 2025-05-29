package com.notfound.lpickbackend.userinfo.command.application.service;

import com.notfound.lpickbackend.common.redis.RedisService;
import com.notfound.lpickbackend.userinfo.command.application.dto.LogoutRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserInfoCommandService extends DefaultOAuth2UserService {

    private final RedisService redisService;
    private final int accessTokenValidity;

    public UserInfoCommandService(RedisService redisService, @Value("${token.access_token_expiration_time}") int accessTokenValidity) {
        this.redisService = redisService;
        this.accessTokenValidity = accessTokenValidity;
    }

    public void logout(LogoutRequestDTO logoutRequestDTO) {

        // whiteList에서 OAuthId로 RefreshToken 삭제
        redisService.deleteRefreshToken(logoutRequestDTO.getOAuthId());
        // BlackList에 AccessToken 추가
        redisService.saveBlacklistAccessToken(logoutRequestDTO.getAccessToken(), accessTokenValidity, TimeUnit.MICROSECONDS);
    }
}
