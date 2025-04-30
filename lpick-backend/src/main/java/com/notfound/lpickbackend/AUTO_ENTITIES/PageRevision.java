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
@Table(name = "page_revision")
public class PageRevision {
    @Id
    @Column(name = "revision_id", nullable = false, length = 40)
    private String revisionId;

    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    private String content;

    @Column(name = "revision_number", nullable = false, length = 50)
    private String revisionNumber;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "wiki_id", nullable = false, length = 40)
    private String wikiId;

    @Column(name = "oauth_id", nullable = false, length = 40)
    private String oauthId;

}