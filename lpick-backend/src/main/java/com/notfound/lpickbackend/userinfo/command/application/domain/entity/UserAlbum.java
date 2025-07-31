package com.notfound.lpickbackend.userinfo.command.application.domain.entity;

import com.notfound.lpickbackend.servicedata.command.application.domain.Album;
import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_album")
public class UserAlbum {

    @Builder
    public UserAlbum(String userAlbumId, String recordFile, boolean isFavorite, Album album, UserInfo oauth) {
        this.userAlbumId = userAlbumId;
        this.recordFile = recordFile;
        this.album = album;
        this.oauth = oauth;
        this.isFavorite = isFavorite;
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

    @Column(name = "is_favorite") // 10개까지만 설정 가능하게 하기. + 추후 favorite 지정 시간 등으로 정렬조건 추가 할것.
    private boolean isFavorite;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;

}