package com.notfound.lpickbackend.AUTO_ENTITIES;

import com.notfound.lpickbackend.wikipage.command.application.domain.WikiPage;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wiki_id")
    private WikiPage wiki;

}