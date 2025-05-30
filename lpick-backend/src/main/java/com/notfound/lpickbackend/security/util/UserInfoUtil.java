package com.notfound.lpickbackend.security.util;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserInfoUtil {

    // JWT 필터를 거치며 ContextHolder에 저장된 인증 객체 추출
    private static OAuth2UserDetails getAuthentication() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() == null || authentication.getName() == null ||
                !(authentication instanceof UsernamePasswordAuthenticationToken)) {
            throw new CustomException(ErrorCode.SECURITY_CONTEXT_NOT_FOUND);
        }

        return (OAuth2UserDetails) authentication.getPrincipal();
    }

    // 인증 객체에서 로그인 회원의 OAuthId 반환
    public static String getOAuthId() {

        OAuth2UserDetails detail = getAuthentication();
        return detail.getUsername();
    }
}
