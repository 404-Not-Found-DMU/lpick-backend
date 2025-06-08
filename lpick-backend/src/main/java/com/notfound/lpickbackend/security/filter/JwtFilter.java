package com.notfound.lpickbackend.security.filter;

import com.notfound.lpickbackend.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
// 한번만 실행되는 JWT 필터
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // oath2 코드 요청 리다이렉트는 건너 뛰기
        // 개발자 전용 토큰 요청도 건너 뛰기
        if (pathMatcher.match("/oauth2/code/**", path) || 
                path.equals("/") || 
                pathMatcher.match("/api/v1/developer-token", path)
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        // 요청 헤더에서 Cookies 추출
        Cookie[] cookies = request.getCookies();

        String tokenValue = null;

        String token = null;

        // refresh 요청일 경우 refreshToken를, 그 외에는 accessToken을 추출
        if(pathMatcher.match("/api/v1/auth/refresh/**", path)) {
            tokenValue = "refresh_token";
        } else {
            tokenValue = "access_token";
        }

        // cookie에서 토큰 추출
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (tokenValue.equals(cookie.getName())) {
                    token = cookie.getValue();
                    log.info("{} : {}", tokenValue, token);
                }
            }
        }

        // Token에서 토큰 추출
        if (token != null) {

            if (jwtUtil.validateToken(token)) {
                Authentication authentication = jwtUtil.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
