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
@Table(name = "artist_like")
public class ArtistLike {
    @Id
    @Column(name = "artist_like_id", nullable = false, length = 40)
    private String artistLikeId;

    @Column(name = "oauth_id", nullable = false, length = 40)
    private String oauthId;

    @Column(name = "artist_id", nullable = false, length = 40)
    private String artistId;

}