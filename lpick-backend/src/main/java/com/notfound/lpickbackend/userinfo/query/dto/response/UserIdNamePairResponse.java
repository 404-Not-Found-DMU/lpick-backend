package com.notfound.lpickbackend.userInfo.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserIdNamePairResponse {
    @Builder
    public UserIdNamePairResponse(String oauthId, String nickName) {
        this.oauthId = oauthId;
        this.nickName = nickName;
    }

    public String oauthId;
    public String nickName;
}
