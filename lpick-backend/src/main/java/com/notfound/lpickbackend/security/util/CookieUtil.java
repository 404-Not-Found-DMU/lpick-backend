package com.notfound.lpickbackend.security.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/*
* Cookie 생성과 삭제를 담당할 Util 클래스
* 코드 재사용성과 유지보수를 위해 작성
* */
@Component
public class CookieUtil {

    // 쿠키 추가
    // secure(true) + sameSite("None") 설정은 쿠키를 위해 필수에 가깝습니다.
    // 해당 설정이 존재해야 Cross-Origin(백엔드 도메인과 다른 경우에도 호출 허용), 쿠키 저장 등의 설정을 동시에 진행할 수 있습니다.

    @Value("${spring.cookie.secure}")
    private final boolean secure;

    @Value("${spring.cookie.same-site}")
    private final String sameSite;

    @Value("${cookie.domain}")
    private final String domain;

    @Value("${cookie.partitioned}")
    private final boolean partitioned;

    // 생성자 주입을 통해 final 필드를 초기화합니다.
    public CookieUtil (
            @Value("${spring.cookie.secure}") boolean secure,
            @Value("${spring.cookie.same-site}") String sameSite,
            @Value("${cookie.domain}") String domain, boolean partitioned) {

        this.secure = secure;
        this.sameSite = sameSite;
        this.domain = domain;
        this.partitioned = partitioned;
    }

    public void addCookie(HttpServletResponse response, String name, String value, int maxAgeInSec) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure) // https만 쿠키 전달
                .sameSite(sameSite) // Cross-Origin 허용
                .path("/")
                .domain(domain)
                .maxAge(maxAgeInSec)
                .partitioned(partitioned)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    // 쿠키 삭제
    public void deleteCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(secure) // https만 쿠키 전달
                .sameSite(sameSite) // Cross-Origin 허용
                .path("/")
                .domain(domain)
                .maxAge(0)
                .partitioned(partitioned)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
