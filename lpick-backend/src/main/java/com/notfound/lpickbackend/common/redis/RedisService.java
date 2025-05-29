package com.notfound.lpickbackend.common.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    // 블랙리스트 등록
    public void saveBlacklistAccessToken(String accessToken, long duration, TimeUnit unit) {
        String key = "blacklist:access:" + accessToken;
        redisTemplate.opsForValue().set(key, "true", duration, unit);
    }

    // 블랙리스트 확인
    public boolean isBlacklisted(String accessToken) {
        String key = "blacklist:access:" + accessToken;
        return Boolean.TRUE.toString().equals(redisTemplate.opsForValue().get(key));
    }

    // Refresh 토큰 저장
    public void saveWhitelistRefreshToken(String userId, String refreshToken, long duration, TimeUnit unit) {
        String key = "refresh:" + userId;
        log.info("white : {}", refreshToken);
        redisTemplate.opsForValue().set(key, refreshToken, duration, unit);
    }

    // Refresh 토큰 유효성 확인
    public boolean isValidRefreshToken(String userId, String providedToken) {
        String key = "refresh:" + userId;
        String savedToken = redisTemplate.opsForValue().get(key);
        return providedToken.equals(savedToken);
    }

    // Refresh 토큰 삭제 (로그아웃 또는 재발급 시)
    public void deleteRefreshToken(String userId) {
        String key = "refresh:" + userId;
        redisTemplate.delete(key);
    }
}
