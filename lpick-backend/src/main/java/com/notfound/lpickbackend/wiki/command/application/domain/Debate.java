package com.notfound.lpickbackend.wiki.command.application.domain;

import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
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
@Table(name = "debate")
public class Debate {
    @Id
    @Column(name = "dt_id", nullable = false, length = 40)
    private String dtId;

    @PrePersist
    public void prePersist() {
        if (this.dtId == null) {
            this.dtId = IdPrefixUtil.get(this.getClass().getSimpleName()) + "_" + UUID.randomUUID();
        }
    }

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "debate_name", nullable = false, length = 50)
    private String debateName;

    @Column(name = "debate_subject", nullable = false, length = 15)
    private DebateSubject debateSubject;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_end", nullable = false, length = 10)
    private DebateStatus isEnd;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wiki_id", nullable = false)
    private WikiPage wiki;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "revision", nullable = false)
    private PageRevision pageRevision;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;

    /**
     * 종료된 토론을 다시 여는 경우는 다루지 않음.
     */
    public void patchStatus(DebateStatus status) {

        if (DebateStatus.CLOSE == status ||
                DebateStatus.VOTE == status) {
            this.isEnd = status;
        } else if (DebateStatus.OPEN == status) {
            throw new CustomException(ErrorCode.CAN_NOT_OPEN_DEBATE_AGAIN);
        } else {
            throw new CustomException(ErrorCode.ILLEGAL_ENUM_VALUE_DETECTED);
        }

    }


}