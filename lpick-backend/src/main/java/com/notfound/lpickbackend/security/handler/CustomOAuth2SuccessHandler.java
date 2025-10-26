package com.notfound.lpickbackend.security.handler;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.common.redis.RedisService;
import com.notfound.lpickbackend.security.details.CustomOAuthUser;
import com.notfound.lpickbackend.security.util.CookieUtil;
import com.notfound.lpickbackend.security.util.JwtTokenProvider;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
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
    private final CookieUtil cookieUtil;

    public CustomOAuth2SuccessHandler(
            @Value("${token.access_token_expiration_time}"
            ) int accessTokenValidity,
            @Value("${token.refresh_token_expiration_time}"
            ) int refreshTokenValidity,
            UserInfoCommandRepository userInfoCommandRepository,
            JwtTokenProvider jwtTokenProvider,
            RedisService redisService, CookieUtil cookieUtil
    ) {
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
        this.userInfoCommandRepository = userInfoCommandRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
        this.cookieUtil = cookieUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        log.info("kakao Login start");

        CustomOAuthUser oAuthUser = (CustomOAuthUser) authentication.getPrincipal();
        String oAuthId = oAuthUser.getName(); // CustomOAuthUser의 oAuthID return받음
        UserInfo userInfo = userInfoCommandRepository.findByOauthId(oAuthId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        log.info("kakao Login Success : {}", oAuthId);

        // accessToken, refreshToken 생성
        String accessToken = jwtTokenProvider.createAccessToken(oAuthId, userInfo);
        String refreshToken = jwtTokenProvider.createRefreshToken(oAuthId, userInfo);

        // 쿠키에 저장
        cookieUtil.addCookie(response, "access_token", accessToken, accessTokenValidity); // 1시간
        cookieUtil.addCookie(response, "refresh_token", refreshToken, refreshTokenValidity); // 7일

        // redis whiteList에 refreshToken 저장
        redisService.saveWhitelistRefreshToken(oAuthId, refreshToken, refreshTokenValidity, TimeUnit.MILLISECONDS);

        log.warn("login success");

        String host = getEffectiveHost(request);
        log.warn(host);
        if(userInfo.getAbout() == null || userInfo.getAbout().isEmpty() || userInfo.getAbout().isBlank()) {
            if(isLocalhost(host)) {
                response.sendRedirect("http://localhost:3000/signup");
            } else {
                response.sendRedirect("https://lpick.in/signup");
            }
        } else {
            if(isLocalhost(host)) {
                response.sendRedirect("http://localhost:3000/");
            } else {
                response.sendRedirect("https://lpick.in/");
            }
        }
    }

    /** 프록시 환경 고려하여 유효한 Host 선택 */
    private String getEffectiveHost(HttpServletRequest req) {
        String xfHost = req.getHeader("X-Forwarded-Host");
        if (xfHost != null && !xfHost.isBlank()) return xfHost;
        String host = req.getHeader("Host");
        if (host != null && !host.isBlank()) return host;
        return req.getServerName();
    }

    /** 로컬호스트 판별 */
    private boolean isLocalhost(String host) {
        if (host == null) return false;
        String h = host.toLowerCase();
        return h.contains("localhost") || h.startsWith("127.0.0.1") || h.startsWith("0.0.0.0");
    }

}
