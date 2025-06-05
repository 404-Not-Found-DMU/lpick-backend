package com.notfound.lpickbackend.temp.query.dto.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class UserIdNamePairResponse {
    @Builder
    public UserIdNamePairResponse(String oauthId, String nickName) {
        this.oauthId = oauthId;
        this.nickName = nickName;
    }

    public String oauthId;
    public String nickName;
}
