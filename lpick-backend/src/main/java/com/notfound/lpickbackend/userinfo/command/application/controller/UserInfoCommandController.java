package com.notfound.lpickbackend.userinfo.command.application.controller;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.security.util.CookieUtil;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.command.application.dto.infodto.LogoutRequestDTO;
import com.notfound.lpickbackend.userinfo.command.application.dto.infodto.TokenRefreshRequestDTO;
import com.notfound.lpickbackend.userinfo.command.application.dto.infodto.TokenResponseDTO;
import com.notfound.lpickbackend.userinfo.command.application.dto.infodto.UserRegistrationRequest;
import com.notfound.lpickbackend.userinfo.command.application.service.UserInfoCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/api/v1")
@Tag(name = "유저 정보 컨트롤러", description = "로그아웃, 쿠키 재요청, 사용자 정보 요청, 사용자 삭제(테스트 위함) 기능")
public class UserInfoCommandController {

    private final UserInfoCommandService userCommandService;
    private final int accessTokenValidity;
    private final int refreshTokenValidity;
    private final CookieUtil cookieUtil;

    public UserInfoCommandController(
            UserInfoCommandService userCommandService,
            @Value("${token.access_token_expiration_time}") int accessTokenValidity,
            @Value("${token.refresh_token_expiration_time}") int refreshTokenValidity, CookieUtil cookieUtil
    ) {
        this.userCommandService = userCommandService;
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
        this.cookieUtil = cookieUtil;
    }

    /*
     * @CookieValue : HttpServletRequest 에서 해당 Value의 쿠키 추출 후 매개변수 주입.
     * required 속성 : 해당 쿠키가 존재하지 않을경우 예외처리를 할것인지 안할것인지.. false로 할 시 예외처리 하지 않음.
     *                CustomError 처리를 위해 false로 설정.
     * @CookieValue는 스프링 MVC 컨트롤러 메서드 파라미터에서만 동작하는 애노테이션.
     * */
    @PostMapping("/auth/logout")
    @Operation(summary = "로그아웃", description = "쿠키와 토큰을 삭제 처리 하는 기능")
    ResponseEntity<SuccessCode> oAuthLogoutRequest(
            HttpServletResponse response,
            @CookieValue(name = "access_token", required = false) String accessToken
    ) {
        if (accessToken == null || accessToken.isEmpty()) { // Cookie에 AccessToken이 없을 경우 바로 로그아웃
            return ResponseEntity.ok(SuccessCode.LOGOUT_SUCCESS);
        }

        // 인증 객체에서 OAuthId 추출
        String oAuthId = UserInfoUtil.getOAuthId();

        userCommandService.logout(new LogoutRequestDTO(accessToken, oAuthId));

        // 보안을 위한 기존 쿠키 삭제
        cookieUtil.deleteCookie(response, "access_token");
        cookieUtil.deleteCookie(response, "refresh_token");
        cookieUtil.deleteCookie(response, "JSESSIONID");

        return ResponseEntity.ok(SuccessCode.LOGOUT_SUCCESS);
    }

    // refresh Token 재발급 요청
    @PostMapping("/auth/refresh")
    @Operation(summary = "토큰 재발급", description = "RefreshToken을 사용해 토큰을 재발급 받는 기능")
    ResponseEntity<SuccessCode> oAuthRefreshTokenRequest(
            HttpServletResponse response,
            @CookieValue(name = "access_token", required = false) String accessToken
    ) {

        // 인증 객체에서 OAuthId 추출
        String oAuthId = UserInfoUtil.getOAuthId();

        TokenResponseDTO tokenResponseDTO = userCommandService.refresh(new TokenRefreshRequestDTO(oAuthId, accessToken));

        // 쿠키 추가
        cookieUtil.addCookie(
                response,
                "access_token",
                tokenResponseDTO.getAccessToken(),
                accessTokenValidity / 1000 // 초 단위라 나누기 1000
        );
        cookieUtil.addCookie(
                response,
                "refresh_token",
                tokenResponseDTO.getRefreshToken(),
                refreshTokenValidity / 1000 // 초 단위라 나누기 1000
        );

        return ResponseEntity.ok(SuccessCode.REFRESH_SUCCESS);
    }

    @PostMapping(
            value = "/auth/registration",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "최초 회원가입", description = "최초 로그인 시 자기소개와 프로필사진을 등록하는 기능")
    ResponseEntity<SuccessCode> userRegistration(
            @RequestPart(name = "userinfo") UserRegistrationRequest userInfo,
            @RequestPart(name = "profileImage") MultipartFile profileImage
    ) {

        String oAuthId = UserInfoUtil.getOAuthId();

        userCommandService.userRegistration(oAuthId, userInfo, profileImage);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }

    /* 개발자 전용 토큰 요청 api */
    @PostMapping("/developer-token")
    @Operation(summary = "개발자 전용 토큰 요청", description = "테스트를 위해 1년짜리 토큰을 발급하는 기능")
    ResponseEntity<TokenResponseDTO> developerTokenRequest(
            HttpServletResponse response
    ) {

        TokenResponseDTO tokenResponseDTO = userCommandService.getDeveloperToken();

        return ResponseEntity.ok(tokenResponseDTO);
    }

    @PatchMapping("/user-info/lpti")
    @Operation(summary = "LPTI 등록", description = "사용자의 LPTI 검사 결과를 등록합니다.")
    ResponseEntity<SuccessCode> lptiRequest(
            @RequestParam(name = "lpti") String lpti
    ) {

        userCommandService.updateLPTI(lpti);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }

    @DeleteMapping("/auth/{oauthId}")
    @Operation(summary = "회원정보 삭제 테스트", description = "최초 회원 가입 테스트를 위한 삭제 메소드입니다.")
    ResponseEntity<SuccessCode> deleteUserInfo(
            @PathVariable String oauthId) {

        userCommandService.deleteUserInfo(oauthId);

        return ResponseEntity.ok(SuccessCode.DELETE_SUCCESS);
    }


}
