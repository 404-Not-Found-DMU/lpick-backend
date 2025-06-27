package com.notfound.lpickbackend.userinfo.query.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/** 사용자가 소유중인 앨범의 정보 기입하는 response*/
@Getter
@Builder
public class UserAlbumOwnedResponse {
    private String userAlbumId;
    private String name;
    private String profile; // 앨범커버 
    private String artistName;
    private String recordFile; // 유무에 따라 null로 기입.
    private Instant releaseDate;
    private String releaseCountry;
    private String label;
}
