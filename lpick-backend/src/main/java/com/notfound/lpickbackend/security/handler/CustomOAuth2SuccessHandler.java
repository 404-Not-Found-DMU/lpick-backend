package com.notfound.lpickbackend.security.handler;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.common.redis.RedisService;
import com.notfound.lpickbackend.security.details.CustomOAuthUser;
import com.notfound.lpickbackend.security.util.CookieUtil;
import com.notfound.lpickbackend.security.util.JwtTokenProvider;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import com.notfound.lpickbackend.userinfo.command.repository.UserInfoCommandRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserInfoCommandRepository userInfoCommandRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    private final int accessTokenValidity;
    private final int refreshTokenValidity;

    public CustomOAuth2SuccessHandler(
            @Value("${token.access_token_expiration_time}"
            ) int accessTokenValidity,
            @Value("${token.refresh_token_expiration_time}"
            ) int refreshTokenValidity,
            UserInfoCommandRepository userInfoCommandRepository,
            JwtTokenProvider jwtTokenProvider,
            RedisService redisService
    ) {
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
        this.userInfoCommandRepository = userInfoCommandRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomOAuthUser oAuthUser = (CustomOAuthUser) authentication.getPrincipal();

        String oAuthId = oAuthUser.getName(); // CustomOAuthUser의 oAuthID return받음

        UserInfo userInfo = userInfoCommandRepository.findByOauthId(oAuthId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        // accessToken, refreshToken 생성
        String accessToken = jwtTokenProvider.createAccessToken(oAuthId, userInfo);
        String refreshToken = jwtTokenProvider.createRefreshToken(oAuthId, userInfo);

        // 쿠키에 저장
        CookieUtil.addCookie(response, "access_token", accessToken, accessTokenValidity/1000); // 1시간
        CookieUtil.addCookie(response, "refresh_token", refreshToken, refreshTokenValidity/1000); // 7일

        // redis whiteList에 refreshToken 저장
        redisService.saveWhitelistRefreshToken(oAuthId, refreshToken, refreshTokenValidity, TimeUnit.MILLISECONDS);

        // redirect : 아직 보낼곳이 없어서 임시로 작성
        response.sendRedirect("/");
    }

}
