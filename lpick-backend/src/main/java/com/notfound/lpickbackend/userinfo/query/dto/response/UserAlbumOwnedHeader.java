package com.notfound.lpickbackend.userinfo.query.dto.response;

import lombok.Getter;

@Getter
public class UserAlbumOwnedHeader {
    private String userAlbumId;
    private String name;
    private String profile; // 앨범커버
    private String artistName;
    private boolean isFavorite;

    public UserAlbumOwnedHeader(String userAlbumId, String name, String profile, String artistName, boolean isFavorite) {
        this.userAlbumId = userAlbumId;
        this.name = name;
        this.profile = profile;
        this.artistName = artistName;
        this.isFavorite = isFavorite;
    }
}
