package com.notfound.lpickbackend.AUTO_ENTITIES;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_album")
public class UserAlbum {
    @Id
    @Column(name = "user_album_id", nullable = false, length = 40)
    private String userAlbumId;

    @Column(name = "record_file", length = 200)
    private String recordFile;

    @Column(name = "album_id", nullable = false, length = 40)
    private String albumId;

    @Column(name = "oauth_id", nullable = false, length = 40)
    private String oauthId;

}