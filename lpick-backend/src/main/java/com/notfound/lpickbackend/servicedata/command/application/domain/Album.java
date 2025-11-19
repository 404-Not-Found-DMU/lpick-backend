package com.notfound.lpickbackend.servicedata.command.application.domain;

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

    @Column(name = "lpti", length = 20)
    private String lpti;

    // DB가 알아서 생성하므로 insertable = false 설정 (혹은 @PrePersist 사용 가능)
    @Column(name = "random_point", insertable = false, updatable = false)
    private Double randomPoint;

    @OneToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "wiki_id")
    private WikiPage wiki;

}