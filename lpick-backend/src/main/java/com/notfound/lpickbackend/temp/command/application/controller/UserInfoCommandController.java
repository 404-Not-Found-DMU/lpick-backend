package com.notfound.lpickbackend.temp.command.application.controller;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.temp.command.application.dto.LogoutRequestDTO;
import com.notfound.lpickbackend.temp.command.application.service.UserInfoCommandService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserInfoCommandController {

    private final UserInfoCommandService userCommandService;

    @PostMapping("/logout")
    ResponseEntity<SuccessCode> oAuthLogoutRequest(HttpServletRequest request) {

        // 요청 헤더에서 accessToken 추출
        String accessToken = getAccessTokenFromCookies(request);
        if (accessToken == null || accessToken.isEmpty()) { // Cookie에 AccessToken이 없을 경우 예외처리
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }

        // 인증 객체에서 OAuthId 추출
        String oAuthId = UserInfoUtil.getOAuthId();

        userCommandService.logout(new LogoutRequestDTO(accessToken, oAuthId));

        return ResponseEntity.ok(SuccessCode.LOGOUT_SUCCESS);
    }

    // Cookie에서 AccessToken 추출하는 메소드
    private String getAccessTokenFromCookies(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if ("accessToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
