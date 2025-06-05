package com.notfound.lpickbackend.userinfo.command.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserTier {
    BRONZE(10),
    SILVER(20),
    GOLD(30),
    DIAMOND(40),
    CHALLENGER(50),
    EXPERT(100),
    ADMIN(9999);

    private final int level;

    // Spring Security의 @PreAuthorize("hasRole('TIER_{{value}}')")을 쓰려면 "ROLE_" 접두사를 붙여야 함
    //
    public String authority() {
        return "ROLE_TIER_" + name();
    }

    public boolean isAtLeast(UserTier other) {
        return this.level >= other.level;
    }
}
