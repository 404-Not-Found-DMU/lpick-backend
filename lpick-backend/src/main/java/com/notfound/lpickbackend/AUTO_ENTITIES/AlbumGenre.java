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
@Table(name = "album_genre")
public class AlbumGenre {
    @Id
    @Column(name = "album_genre_id", nullable = false, length = 40)
    private String albumGenreId;

    @Column(name = "album_id", nullable = false, length = 40)
    private String albumId;

    @Column(name = "genre_id", nullable = false, length = 40)
    private String genreId;

}