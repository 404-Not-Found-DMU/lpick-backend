package com.notfound.lpickbackend.userinfo.command.application.domain;

import com.notfound.lpickbackend.AUTO_ENTITIES.Album;
import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_album")
public class UserAlbum {

    @Builder
    public UserAlbum(String userAlbumId, String recordFile, Album album, UserInfo oauth) {
        this.userAlbumId = userAlbumId;
        this.recordFile = recordFile;
        this.album = album;
        this.oauth = oauth;
    }

    @PrePersist
    public void prePersist() {
        if (this.userAlbumId == null) {
            this.userAlbumId = IdPrefixUtil.get(this.getClass().getSimpleName()) + "_" + UUID.randomUUID();
        }
    }

    @Id
    @Column(name = "user_album_id", nullable = false, length = 40)
    private String userAlbumId;

    @Column(name = "record_file", length = 200)
    private String recordFile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;

}