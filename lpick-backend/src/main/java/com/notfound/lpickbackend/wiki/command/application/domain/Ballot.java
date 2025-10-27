package com.notfound.lpickbackend.wiki.command.application.domain;

import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "ballot",
        uniqueConstraints = {
                // 한 사용자는 특정 토론에 한 번만 투표
                @UniqueConstraint(name = "uq_ballot_dt_oauth", columnNames = {"dt_id", "oauth_id"})
        }
//        indexes = {
//                @Index(name = "idx_ballot_dt_id", columnList = "dt_id"),
//                @Index(name = "idx_ballot_oauth_id", columnList = "oauth_id")
//        }
)
public class Ballot {

    @Id
    @Column(name = "blt_id", length = 40, nullable = false)
    private String bltId;

    @PrePersist
    public void prePersist() {
        if (this.bltId == null) {
            this.bltId = IdPrefixUtil.get(this.getClass().getSimpleName()) + "_" + UUID.randomUUID();
        }
    }

    // 사용자 FK (oauth_id)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;   // 기존 Oauth 엔티티의 @Id가 oauth_id여야 함

    // 토론 FK (dt_id)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dt_id", nullable = false)
    private Debate debate;

    // 투표값 (예: AGREE/DISAGREE/ABSTAIN 등)
    @Enumerated(EnumType.STRING)
    @Column(name = "ballot_value", length = 10, nullable = false)
    private BallotValue ballotValue;
}