package com.notfound.lpickbackend.wiki.command.application.domain;

import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "debate")
public class Debate {
    @Id
    @Column(name = "dt_id", nullable = false, length = 40)
    private String dtId;

    public void prePersist() {
        if (this.dtId == null) {
            this.dtId = IdPrefixUtil.get(this.getClass().getSimpleName()) + "_" + UUID.randomUUID();
        }
    }

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "is_end", nullable = false, length = 10)
    private String isEnd;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wiki_id", nullable = false)
    private WikiPage wiki;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;

}