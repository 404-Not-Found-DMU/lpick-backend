package com.notfound.lpickbackend.security.util;

import com.notfound.lpickbackend.AUTO_ENTITIES.Tier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserInfoCreateDTO {

    private String oauthId;

    private String oauthType;

    private String nickname;

    private String profile;

    private Integer point;

    private Integer stackPoint;

    private String about;

    private String lpti;

    private Tier tier;
}
