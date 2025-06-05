package com.notfound.lpickbackend.security.details;

import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/* LPick에서 발급하는 JWT 인증을 위한 UserDetails */
public class OAuth2UserDetails implements UserDetails {

    UserInfo userInfo;

    public OAuth2UserDetails(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
// 추후 권한 생성 시 활용
//        return userInfo.getUserAuthList().stream()
//                .map(userAuth -> new SimpleGrantedAuthority(userAuth.getAuth().getName()))
//                .collect(Collectors.toList());
        return List.of();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return userInfo.getOauthId();
    }
}
