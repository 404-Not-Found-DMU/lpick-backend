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
@Table(name = "debate")
public class Debate {
    @Id
    @Column(name = "dt_id", nullable = false, length = 40)
    private String dtId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "is_end", nullable = false, length = 10)
    private String isEnd;

    @Column(name = "wiki_id", nullable = false, length = 40)
    private String wikiId;

    @Column(name = "oauth_id", nullable = false, length = 40)
    private String oauthId;

}