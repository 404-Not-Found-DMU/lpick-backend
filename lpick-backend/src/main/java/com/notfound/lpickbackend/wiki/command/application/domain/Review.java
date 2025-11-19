package com.notfound.lpickbackend.wiki.command.application.domain;

import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.wiki.command.application.dto.request.ReviewPostRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "review")
public class Review {
    @Id
    @Column(name = "review_id", nullable = false, length = 40)
    private String reviewId;

    @Column(name = "star", nullable = false)
    private Float star;

    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    private String content;

    @CreatedDate
    @Column(name="created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wiki_id", nullable = false)
    private WikiPage wiki;

    public void updateReview(ReviewPostRequest req) {
        this.star = req.getStarScore();
        this.content = req.getContent();
        this.createdAt = Instant.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.reviewId == null) {
            this.reviewId = IdPrefixUtil.get(this.getClass().getSimpleName()) + "_" + UUID.randomUUID();
        }
    }

}