package com.notfound.lpickbackend.AUTO_ENTITIES;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "page_revision")
public class PageRevision {
    @Builder
    public PageRevision(String revisionId, String content, String revisionNumber, Instant createdAt, WikiPage wiki, UserInfo userInfo) {
        this.revisionId = revisionId;
        this.content = content;
        this.revisionNumber = revisionNumber;
        this.createdAt = createdAt;
        this.wiki = wiki;
        this.userInfo = userInfo;
    }

    @Id
    @Column(name = "revision_id", nullable = false, length = 40)
    private String revisionId;

    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    private String content;

    @Column(name = "revision_number", nullable = false, length = 50)
    private String revisionNumber;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wiki_id", nullable = false)
    private WikiPage wiki;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo userInfo;

}