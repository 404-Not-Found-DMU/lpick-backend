package com.notfound.lpickbackend.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

/*
* Cookie 생성과 삭제를 담당할 Util 클래스
* 코드 재사용성과 유지보수를 위해 작성
* */
public class CookieUtil {
    
    // 쿠키 추가 메소드
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAgeInSec) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeInSec);
        response.addCookie(cookie);
    }
    
    // 쿠키 삭제 메소드
    public static void deleteCookie(HttpServletResponse response, String name) {
        
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
}
