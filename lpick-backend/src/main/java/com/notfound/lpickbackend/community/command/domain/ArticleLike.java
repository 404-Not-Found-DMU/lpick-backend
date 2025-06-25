package com.notfound.lpickbackend.community.command.domain;

import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "article_like")
public class ArticleLike {
    @Id
    @Column(name = "article_like_id", nullable = false, length = 40)
    private String articleLikeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @PrePersist
    public void prePersist() {
        if (this.articleLikeId == null)
            this.articleLikeId = IdPrefixUtil.get(this.getClass().getSimpleName()) + "_" + UUID.randomUUID();
    }
}