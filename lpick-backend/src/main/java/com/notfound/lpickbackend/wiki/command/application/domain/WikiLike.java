package com.notfound.lpickbackend.wiki.command.application.domain;

import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "wiki_like")
public class WikiLike {
    @Id
    @Column(name = "wiki_like_id", nullable = false, length = 40)
    private String wikiLikeId;

    @PrePersist
    public void prePersist() {
        if (this.wikiLikeId == null) {
            this.wikiLikeId = IdPrefixUtil.get(this.getClass().getSimpleName()) + "_" + UUID.randomUUID();
        }
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wiki_id", nullable = false)
    private WikiPage wiki;

}
