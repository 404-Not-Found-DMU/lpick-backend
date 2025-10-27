package com.notfound.lpickbackend.userinfo.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/** 사용자가 소유중인 앨범의 정보 기입하는 response*/
@Getter
public class UserAlbumOwnedResponse {
    private String userAlbumId;
    private String name;
    private String profile; // 앨범커버
    private Instant createdAt;
    private String artistName;
    private String recordFile; // 유무에 따라 null로 기입.
    private Instant releaseDate;
    private String releaseCountry;
    private String label;
    private boolean isFavorite;

    public UserAlbumOwnedResponse(String userAlbumId, String name, String profile, Instant createdAt, String artistName, String recordFile, Instant releaseDate, String releaseCountry, String label, boolean isFavorite) {
        this.userAlbumId = userAlbumId;
        this.name = name;
        this.profile = profile;
        this.createdAt = createdAt;
        this.artistName = artistName;
        this.recordFile = recordFile;
        this.releaseDate = releaseDate;
        this.releaseCountry = releaseCountry;
        this.label = label;
        this.isFavorite = isFavorite;
    }
}
