package com.notfound.lpickbackend.userinfo.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.common.redis.RedisService;
import com.notfound.lpickbackend.security.util.JwtTokenProvider;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import com.notfound.lpickbackend.userinfo.command.application.dto.LogoutRequestDTO;
import com.notfound.lpickbackend.userinfo.command.application.dto.TokenRefreshRequestDTO;
import com.notfound.lpickbackend.userinfo.command.application.dto.TokenResponseDTO;
import com.notfound.lpickbackend.userinfo.command.repository.UserInfoCommandRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserInfoCommandService extends DefaultOAuth2UserService {

    private final RedisService redisService;
    private final int accessTokenValidity;
    private final int refreshTokenValidity;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserInfoCommandRepository userInfoCommandRepository;

    public UserInfoCommandService(
            RedisService redisService,
            @Value("${token.access_token_expiration_time}") int accessTokenValidity,
            @Value("${token.refresh_token_expiration_time}") int refreshTokenValidity,
            JwtTokenProvider jwtTokenProvider,
            UserInfoCommandRepository userInfoCommandRepository
    ) {
        this.redisService = redisService;
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userInfoCommandRepository = userInfoCommandRepository;
    }

    public void logout(LogoutRequestDTO logoutRequestDTO) {

        // Token 무효화
        invalidateTokens(logoutRequestDTO.getOAuthId(), logoutRequestDTO.getAccessToken());

    }

    public TokenResponseDTO refresh(TokenRefreshRequestDTO tokenRefreshRequestDTO) {

        String oAuthId = tokenRefreshRequestDTO.getOAuthId();

        // Token 무효화
        invalidateTokens(oAuthId, tokenRefreshRequestDTO.getAccessToken());

        UserInfo userInfo = userInfoCommandRepository.findByOauthId(oAuthId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        // accessToken, refreshToken 생성
        String accessToken = jwtTokenProvider.createAccessToken(oAuthId, userInfo);
        String refreshToken = jwtTokenProvider.createRefreshToken(oAuthId, userInfo);

        // redis whiteList에 refreshToken 저장
        redisService.saveWhitelistRefreshToken(oAuthId, refreshToken, refreshTokenValidity, TimeUnit.MILLISECONDS);

        return new TokenResponseDTO(accessToken, refreshToken);
    }

    /*
     * AccessToken은 BlackList에 추가, RefreshToken은 White에서 삭제합니다.
     * RefreshToken 재사용 방지를 위해 AccessToken 및 RefreshToken은 발급과 삭제가 동시에 이루어집니다.
     * 고민점 : logout과 refresh에 필요한 데이터는 OAuthId, AccessToken으로 동일하기에 두 로직에서 사용되는 DTO를 나눌지, 하나로 사용할지 고민
     * */
    private void invalidateTokens(String oAuthId, String accessToken) {

        // whiteList에서 OAuthId로 RefreshToken 삭제
        redisService.deleteRefreshToken(oAuthId);
        // BlackList에 AccessToken 추가
        redisService.saveBlacklistAccessToken(accessToken, accessTokenValidity, TimeUnit.MILLISECONDS);
    }

    /* 개발자용 토큰 생성 api */
    public TokenResponseDTO getDeveloperToken() {

        /* 더미데이터에 존재하는 OAuthId '1' 유저 */
        UserInfo userInfo = userInfoCommandRepository.findByOauthId("1").orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        // 개발자용 accessToken, refreshToken 생성
        String accessToken = jwtTokenProvider.createDevAccessToken("1", userInfo);
        String refreshToken = jwtTokenProvider.createDevRefreshToken("1", userInfo);

        // redis whiteList에 refreshToken 1년 동안 저장
        redisService.saveWhitelistRefreshToken("1", refreshToken, 365, TimeUnit.DAYS);

        return new TokenResponseDTO(accessToken, refreshToken);
    }
}
