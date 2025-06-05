package com.notfound.lpickbackend.AUTO_ENTITIES;

import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @OneToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "wiki_id")
    private WikiPage wiki;

}