package com.notfound.lpickbackend.AUTO_ENTITIES;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "page_revision")
public class PageRevision {

    @Builder
    public PageRevision(String content, String revisionNumber, Instant createdAt, String wikiId, String oauthId) {
        this.content = content;
        this.revisionNumber = revisionNumber;
        this.createdAt = createdAt;
        this.wikiId = wikiId;
        this.oauthId = oauthId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "revision_id", nullable = false, length = 40)
    private String revisionId;

    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    private String content;

    @Column(name = "revision_number", nullable = false, length = 50)
    private String revisionNumber;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "wiki_id", nullable = false, length = 40)
    private String wikiId;

    @Column(name = "oauth_id", nullable = false, length = 40)
    private String oauthId;

}