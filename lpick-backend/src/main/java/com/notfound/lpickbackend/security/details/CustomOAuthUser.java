package com.notfound.lpickbackend.security.details;

import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class CustomOAuthUser implements OAuth2User {

    private final UserInfo userInfo;

    public CustomOAuthUser(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
// 추후 권한 생성시 활용
//        return userInfo.getUserAuthList().stream()
//                .map(userAuth -> new SimpleGrantedAuthority(userAuth.getAuth().getName()))
//                .collect(Collectors.toList());
        return List.of();
    }

    @Override
    public String getName() {
        return userInfo.getOauthId();
    }

}
