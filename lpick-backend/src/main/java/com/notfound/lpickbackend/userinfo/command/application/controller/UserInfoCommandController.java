package com.notfound.lpickbackend.userinfo.command.application.controller;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.security.util.CookieUtil;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.command.application.dto.LogoutRequestDTO;
import com.notfound.lpickbackend.userinfo.command.application.dto.TokenRefreshRequestDTO;
import com.notfound.lpickbackend.userinfo.command.application.dto.TokenResponseDTO;
import com.notfound.lpickbackend.userinfo.command.application.service.UserInfoCommandService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class UserInfoCommandController {

    private final UserInfoCommandService userCommandService;
    private final int accessTokenValidity;
    private final int refreshTokenValidity;

    public UserInfoCommandController(
            UserInfoCommandService userCommandService,
            @Value("${token.access_token_expiration_time}") int accessTokenValidity,
            @Value("${token.refresh_token_expiration_time}") int refreshTokenValidity
    ) {
        this.userCommandService = userCommandService;
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    /*
    * @CookieValue : HttpServletRequest 에서 해당 Value의 쿠키 추출 후 매개변수 주입.
    * required 속성 : 해당 쿠키가 존재하지 않을경우 예외처리를 할것인지 안할것인지.. false로 할 시 예외처리 하지 않음.
    *                CustomError 처리를 위해 false로 설정.
    * @CookieValue는 스프링 MVC 컨트롤러 메서드 파라미터에서만 동작하는 애노테이션.
    * */
    @PostMapping("/auth/logout")
    ResponseEntity<SuccessCode> oAuthLogoutRequest(
            HttpServletResponse response,
            @CookieValue(name = "access_token", required = false) String accessToken
    ) {
        if (accessToken == null || accessToken.isEmpty()) { // Cookie에 AccessToken이 없을 경우 예외처리
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }

        // 인증 객체에서 OAuthId 추출
        String oAuthId = UserInfoUtil.getOAuthId();

        userCommandService.logout(new LogoutRequestDTO(accessToken, oAuthId));

        // 보안을 위한 기존 쿠키 삭제
        CookieUtil.deleteCookie(response, "access_token");
        CookieUtil.deleteCookie(response, "refresh_token");

        return ResponseEntity.ok(SuccessCode.LOGOUT_SUCCESS);
    }

    // refresh Token 재발급 요청
    @PostMapping("/auth/refresh")
    ResponseEntity<SuccessCode> oAuthRefreshTokenRequest(
            HttpServletResponse response,
            @CookieValue(name = "access_token", required = false) String accessToken
    ) {

        // 인증 객체에서 OAuthId 추출
        String oAuthId = UserInfoUtil.getOAuthId();

        TokenResponseDTO tokenResponseDTO = userCommandService.refresh(new TokenRefreshRequestDTO(oAuthId, accessToken));

        // 쿠키 추가
        CookieUtil.addCookie(
                response,
                "access_token",
                tokenResponseDTO.getAccessToken(),
                accessTokenValidity
        );
        CookieUtil.addCookie(
                response,
                "refresh_token",
                tokenResponseDTO.getRefreshToken(),
                refreshTokenValidity
        );

        return null;
    }

}
