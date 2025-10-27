package com.notfound.lpickbackend.wiki.command.application.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;


@NoArgsConstructor
@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "page_revision")
public class PageRevision {
    @Builder
    public PageRevision(String revisionId, JsonNode content, String revisionNumber, Instant createdAt, WikiPage wiki, UserInfo userInfo) {
        this.revisionId = revisionId;
        this.content = content;
        this.revisionNumber = revisionNumber;
        this.createdAt = createdAt;
        this.wiki = wiki;
        this.userInfo = userInfo;
    }

    @PrePersist
    public void prePersist() {
        if (this.revisionId == null) {
            this.revisionId = IdPrefixUtil.get(this.getClass().getSimpleName()) + "_" + UUID.randomUUID();
        }
    }

    @Id
    @Column(name = "revision_id", nullable = false, length = 40)
    private String revisionId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private JsonNode content;         // ← 파일 원문을 그대로

    @Column(name = "revision_number", nullable = false, length = 50)
    private String revisionNumber;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wiki_id", nullable = false)
    private WikiPage wiki;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo userInfo;

}