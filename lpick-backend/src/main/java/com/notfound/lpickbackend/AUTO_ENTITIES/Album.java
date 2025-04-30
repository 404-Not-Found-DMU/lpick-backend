package com.notfound.lpickbackend.AUTO_ENTITIES;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "album")
public class Album {
    @Id
    @Column(name = "album_id", nullable = false, length = 40)
    private String albumId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "profile", length = 200)
    private String profile;

    @Column(name = "release_date")
    private Instant releaseDate;

    @Column(name = "release_country", length = 50)
    private String releaseCountry;

    @Column(name = "label", length = 50)
    private String label;

    @Column(name = "wiki_id", length = 40)
    private String wikiId;

}