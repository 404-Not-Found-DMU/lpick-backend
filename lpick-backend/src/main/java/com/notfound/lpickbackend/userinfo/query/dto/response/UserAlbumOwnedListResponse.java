package com.notfound.lpickbackend.userinfo.query.dto.response;

import lombok.Builder;
import lombok.Getter;

/** UserAlbumOwnedResponse 의 나열을 목적으로 하는 Response 클래스*/
@Getter
@Builder
public class UserAlbumOwnedListResponse {
    private String userAlbumOwnedId;
    private String profile;
    private String name;
}
