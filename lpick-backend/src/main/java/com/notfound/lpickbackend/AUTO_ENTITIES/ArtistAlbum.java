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
@Table(name = "artist_album")
public class ArtistAlbum {
    @Id
    @Column(name = "artist_album_id", nullable = false, length = 40)
    private String artistAlbumId;

    @Column(name = "artist_id", nullable = false, length = 40)
    private String artistId;

    @Column(name = "album_id", nullable = false, length = 40)
    private String albumId;

}