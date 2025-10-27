package com.notfound.lpickbackend.wiki.command.application.domain;

import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import com.notfound.lpickbackend.community.command.domain.Comment;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import jakarta.persistence.*;
import lombok.*;
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
@Table(name = "debate_chat")
public class DebateChat {
    @Id
    @Column(name = "dsc_id", nullable = false, length = 40)
    private String dscId;

    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    private String content;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "is_blind", nullable = false, length = 10)
    private boolean isBlind;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dt_id", nullable = false)
    private Debate dt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "parent_debate_chat_id", nullable = true)
    private DebateChat parentDebateChat;

    @PrePersist
    public void prePersist() {
        if (this.dscId == null) {
            this.dscId = IdPrefixUtil.get(this.getClass().getSimpleName()) + "_" + UUID.randomUUID();
        }
    }

}