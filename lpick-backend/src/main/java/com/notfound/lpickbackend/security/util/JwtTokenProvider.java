package com.notfound.lpickbackend.security.util;

import com.notfound.lpickbackend.userinfo.command.application.domain.Auth;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public JwtTokenProvider(@Value("${token.secret}") String secret,
                            @Value("${token.access_token_expiration_time}"
                            ) long accessTokenValidity,
                            @Value("${token.refresh_token_expiration_time}"
                            ) long refreshTokenValidity
    ) {

        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    // Access Token 생성 메서드
    public String createAccessToken(String subject, UserInfo userInfo) {

        Map<String,Object> claims = createClaims(userInfo);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // 사용자 식별자
                .setIssuedAt(now) // 발급 시간
                .setExpiration(new Date(now.getTime() + accessTokenValidity)) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS512) // 서명 알고리즘
                .compact();
    }

    // Refresh Token 생성 메서드
    public String createRefreshToken(String subject, UserInfo userInfo) {

        Map<String,Object> claims = createClaims(userInfo);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // 사용자 식별자
                .setIssuedAt(now) // 발급 시간
                .setExpiration(new Date(now.getTime() + refreshTokenValidity)) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS512) // 서명 알고리즘
                .compact();
    }

    // Access Token 생성 메서드
    public String createDevAccessToken(String subject, UserInfo userInfo) {

        Map<String,Object> claims = createClaims(userInfo);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // 사용자 식별자
                .setIssuedAt(now) // 발급 시간
                .setExpiration(new Date(now.getTime() + 31536000000L)) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS512) // 서명 알고리즘
                .compact();
    }

    // Refresh Token 생성 메서드
    public String createDevRefreshToken(String subject, UserInfo userInfo) {

        Map<String,Object> claims = createClaims(userInfo);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // 사용자 식별자
                .setIssuedAt(now) // 발급 시간
                .setExpiration(new Date(now.getTime() + 31536000000L)) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS512) // 서명 알고리즘
                .compact();
    }

    private Map<String, Object> createClaims(UserInfo userInfo) {

        // Claims에 넣기 위해 List<Auth>에서 List<String>으로 변경
        List<String> authList = userInfo.getAuthorities() // List<Auth>
                .stream()
                .map(Auth::getName) // 권한 이름만 추출
                .toList();

        // Token에 담을 Claim 생성
        Map<String, Object> claims = new HashMap<>();
        claims.put("oAuthId", userInfo.getOauthId());
        claims.put("Tier", userInfo.getTier().getTierId());
        claims.put("authList", authList);

        return claims;
    }
}
