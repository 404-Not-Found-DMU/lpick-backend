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
@Table(name = "artist")
public class Artist {
    @Id
    @Column(name = "artist_id", nullable = false, length = 40)
    private String artistId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "debut_at")
    private Instant debutAt;

    @Column(name = "group_name", length = 50)
    private String groupName;

    @Column(name = "company", length = 50)
    private String company;

    @Column(name = "wiki_id", length = 40)
    private String wikiId;

}