package com.notfound.lpickbackend.userinfo.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponse {
    private String userLevel;
    private Integer nowExp;
    private String limitExp;
    private String profile;
}
