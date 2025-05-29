package com.notfound.lpickbackend.security.util;

import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class OAuthUserDetails implements UserDetails {

    private final UserInfo userInfo;

    public OAuthUserDetails(UserInfo userInfo) {

        this.userInfo = userInfo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return "";
    }
}
