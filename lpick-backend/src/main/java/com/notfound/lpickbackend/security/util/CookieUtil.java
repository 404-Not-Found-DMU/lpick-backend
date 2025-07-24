package com.notfound.lpickbackend.security.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

/*
* Cookie 생성과 삭제를 담당할 Util 클래스
* 코드 재사용성과 유지보수를 위해 작성
* */
public class CookieUtil {

    // 쿠키 추가
    // secure(true) + sameSite("None") 설정은 쿠키를 위해 필수에 가깝습니다.
    // 해당 설정이 존재해야 Cross-Origin(백엔드 도메인과 다른 경우에도 호출 허용), 쿠키 저장 등의 설정을 동시에 진행할 수 있습니다.
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAgeInSec) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true) // https만 쿠키 전달
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
