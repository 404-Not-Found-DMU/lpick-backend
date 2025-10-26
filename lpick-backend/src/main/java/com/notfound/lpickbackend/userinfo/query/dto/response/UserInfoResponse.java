package com.notfound.lpickbackend.userinfo.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserInfoResponse {

    private String oauthId;
    private String nickname;
    private String about;
    private String profile;
    private String lpti;
}
