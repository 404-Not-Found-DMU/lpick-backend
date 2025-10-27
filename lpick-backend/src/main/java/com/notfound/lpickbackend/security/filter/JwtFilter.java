package com.notfound.lpickbackend.security.filter;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.redis.RedisService;
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

    private final RedisService redisService;

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();


//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//        String path = request.getRequestURI();
//
//        /*
//        * 유의사항 - isPublicAPI를 그냥 상단에서 if문으로 한번에 빼버리면 안되는가?
//        * => public API == 비로그인 사용자, 로그인 사용자 '모두' 사용 가능해야함. 즉, 있으면 있는대로 없으면 없는대로 쓰는 식이므로
//        * 토큰 검증 자체는 수행해야하기에 부득이하게 if문으로 여러번 나타납니다.
//        * */
//        boolean isPublicAPI = false; // 해당 내역이 true면, jwt가 존재하지 아니해도
//
//        // oath2 코드 요청 리다이렉트는 건너 뛰기
//        // 개발자 전용 토큰 요청도 건너 뛰기
//        // Oauth 호출과정에서 특정상황에 발생할 수 있는 모든 uri 무시 처리 필요.
//        if (pathMatcher.match("/oauth2/**", path) ||
//                pathMatcher.match("/login/**", path) ||
//                pathMatcher.match("/swagger-ui/**", path) ||
//                pathMatcher.match("/v3/api-docs/**", path) ||
//                pathMatcher.match("/favicon.ico", path) ||
//                pathMatcher.match("/", path) ||
//                pathMatcher.match("/api/v1/developer-token", path) ||
//                pathMatcher.match("/actuator/health", path)
////                || pathMatcher.match("/api/v1/public", path) // /public 계통 요청이 '비로그인 사용자 '전용'' 요청이 되어버리는 문제 해결위해 제거
//        ) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        log.info("JWT Filter 시작. uri : {}", path);
//
//        /** /api/v(N)과 같이 버전별 api별로 동작가능하게 수정  */
//        if(pathMatcher.match("/api/*/public/**", path)) isPublicAPI = true;
//
//        // 요청 헤더에서 Cookies 추출
//        Cookie[] cookies = request.getCookies();
//
//        String tokenValue = null;
//
//        String token = null;
//
//        // refresh 요청일 경우 refreshToken를, 그 외에는 accessToken을 추출
//        if(pathMatcher.match("/api/v1/auth/refresh/**", path)) {
//            tokenValue = "refresh_token";
//        } else {
//            tokenValue = "access_token";
//        }
//
//        // cookie에서 토큰 추출
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (tokenValue.equals(cookie.getName())) {
//                    token = cookie.getValue();
//                    log.info("{} : {}", tokenValue, token);
//                }
//            }
//        } else if(isPublicAPI) {
//            log.info("검증되지 않았으나 public으로 스킵");
//            filterChain.doFilter(request, response); // 쿠키가 없고 public api면 그냥 통과
//            return;
//        }
//
//        // Token에서 토큰 추출
//        if (token != null) {
//            if (jwtUtil.validateToken(token)) {
//                Authentication authentication = jwtUtil.getAuthentication(token);
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//                log.info("검증된 사용자.");
//            } else if(isPublicAPI) {
//                log.info("검증되지 않았으나 public으로 스킵");
//                filterChain.doFilter(request, response);
//                return;
//            }
//        } else if(isPublicAPI) {
//            log.info("검증되지 않았으나 public으로 스킵");
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }


    // 신버전 코드 JwtFilter_V2
    /*
    추가내역 -
    /public api가 '비로그인사용자 전용'이 되던 문제 수정
    인증에 문제가 있는 경우 SpringBoot 자체 처리가 아닌 response 기반으로 필터 내 예외 처리 거치도록 수정
    */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        /*
         * 유의사항 - isPublicAPI를 그냥 상단에서 if문으로 한번에 빼버리면 안되는가?
         * => public API == 비로그인 사용자, 로그인 사용자 '모두' 사용 가능해야함. 즉, 있으면 있는대로 없으면 없는대로 쓰는 식이므로
         * 토큰 검증 자체는 수행해야하기에 부득이하게 if문으로 여러번 나타납니다.
         * */

        // oath2 코드 요청 리다이렉트는 건너 뛰기
        // 개발자 전용 토큰 요청도 건너 뛰기
        // Oauth 호출과정에서 특정상황에 발생할 수 있는 모든 uri 무시 처리 필요.
        if (pathMatcher.match("/oauth2/**", path) ||
                pathMatcher.match("/login/**", path) ||
                pathMatcher.match("/swagger-ui/**", path) ||
                pathMatcher.match("/v3/api-docs/**", path) ||
                pathMatcher.match("/favicon.ico", path) ||
                pathMatcher.match("/", path) ||
                pathMatcher.match("/api/v1/developer-token", path) ||
                pathMatcher.match("/actuator/health", path) ||
                pathMatcher.match("/api/v1/developer-token/cookie", path)
//                || pathMatcher.match("/api/v1/public", path) // /public 계통 요청이 '비로그인 사용자 '전용'' 요청이 되어버리는 문제 해결위해 제거
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("JWT Filter 시작. uri : {}", path);

        /** /api/v(N)과 같이 버전별 api별로 동작가능하게 수정  */

        final boolean isPublic = pathMatcher.match("/api/*/public/**", path);
        final boolean isRefresh = pathMatcher.match("/api/v1/auth/refresh/**", path);

        String tokenName = isRefresh ? "refresh_token" : "access_token";
        String token = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (tokenName.equals(c.getName())) {
                    token = c.getValue();
                    break;
                }
            }
        }

        // 토큰 검증을 시도하는 try - catch 문 리팩토링.(/public 계통 처리 및
        // 추후 필요 내역 모음 :
        // 1. 블랙리스트/화이트리스트 관련 기능 추가해야됨!!
        // 2. response 기반 메시지 반환내역 추후 서버와 동일양식으로 고치기
        // 3.
        try {
            if (token != null && jwtUtil.validateToken(token)) {
                Authentication auth = jwtUtil.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                // 통과
                filterChain.doFilter(request, response);
                return;
            }

            // 여기로 오면: (토큰 없음) 또는 (무효 토큰)
            if (isPublic) {
                // public: 익명으로 통과
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            } else {
                // 보호 리소스: 401 즉시 응답 (체인 진행 금지)
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"message\":\"Invalid or missing token\"}");
                return;
            }
        } catch (CustomException e) {
            // 예외도 위 로직과 동일하게 처리
            SecurityContextHolder.clearContext();
            if (isPublic) {
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"message\":\"Token processing error\"}");
            }
        }
    }



}
