package com.notfound.lpickbackend.AUTO_ENTITIES;

import com.notfound.lpickbackend.servicedata.command.application.domain.Artist;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "artist_like")
public class ArtistLike {
    @Id
    @Column(name = "artist_like_id", nullable = false, length = 40)
    private String artistLikeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

}