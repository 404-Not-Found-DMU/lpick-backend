package com.notfound.lpickbackend.security.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

/*
* Cookie 생성과 삭제를 담당할 Util 클래스
* 코드 재사용성과 유지보수를 위해 작성
* */
public class CookieUtil {

    // 쿠키 추가
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAgeInSec) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true) // 추후 NGINX 등 https 설정 추가시 수정
                .sameSite("None") // Cross-Origin 허용
                .path("/")
                .maxAge(maxAgeInSec)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    // 쿠키 삭제
    public static void deleteCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
