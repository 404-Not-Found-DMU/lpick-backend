package com.notfound.lpickbackend.security.filter;

import com.notfound.lpickbackend.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
        String method = request.getMethod();

//        log.warn(path + " " + method);

        // oath2 코드 요청 리다이렉트는 건너 뛰기
        if (pathMatcher.match("/oauth2/code/**", path) || path.equals("/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 요청 헤더에서 Authorization Header 추출
        String authorizationHeader = request.getHeader("Authorization");
//        log.info("Authorization header: {}", authorizationHeader);

        // Authorization Header에서 Bearer 토큰 추출
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            /* jwt 검증 후 인증객체로 등록 */
            if(jwtUtil.validateToken(token)) {
                Authentication authentication = jwtUtil.getAuthentication(token);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

//        log.info("Jwt Filter doFilter 실행");
        filterChain.doFilter(request, response);
    }
}
