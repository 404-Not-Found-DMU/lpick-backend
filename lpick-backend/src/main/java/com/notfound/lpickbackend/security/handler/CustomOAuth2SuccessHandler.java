package com.notfound.lpickbackend.security.handler;

import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.common.redis.RedisService;
import com.notfound.lpickbackend.security.details.CustomOAuthUser;
import com.notfound.lpickbackend.security.util.JwtTokenProvider;
import com.notfound.lpickbackend.userinfo.command.repository.UserInfoCommandRepository;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
        log.info("oauth2 success");

        CustomOAuthUser oAuthUser = (CustomOAuthUser) authentication.getPrincipal();

        String oAuthId = oAuthUser.getName(); // CustomOAuthUser의 oAuthID return받음

        UserInfo userInfo = userInfoCommandRepository.findById(oAuthId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        // Token에 담을 Claim 생성
        Map<String, Object> claims = new HashMap<>();
        claims.put("oAuthId", userInfo.getOauthType());
        claims.put("oAuthType", userInfo.getOauthType());
        claims.put("Tier", userInfo.getTier().getTierId());

        // accessToken, refreshToken 생성
        String accessToken = jwtTokenProvider.createAccessToken(oAuthId, claims);
        String refreshToken = jwtTokenProvider.createRefreshToken(oAuthId, claims);

        // 쿠키에 저장
        addCookie(response, "access_token", accessToken, accessTokenValidity); // 15분
        addCookie(response, "refresh_token", refreshToken, refreshTokenValidity); // 7일

        // redis whiteList에 refreshToken 저장
        redisService.saveWhitelistRefreshToken(oAuthId, refreshToken, 7, TimeUnit.DAYS);

        // redirect : 아직 보낼곳이 없어서 임시로 작성
        response.sendRedirect("/");
    }

    // 쿠키 추가 메소드
    private void addCookie(HttpServletResponse response, String name, String value, int maxAgeInSec) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeInSec);
        response.addCookie(cookie);
    }
}
