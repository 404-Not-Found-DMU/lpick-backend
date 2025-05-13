package com.notfound.lpickbackend.AUTO_ENTITIES;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "debate_chat")
public class DebateChat {
    @Id
    @Column(name = "dsc_id", nullable = false, length = 40)
    private String dscId;

    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    private String content;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "is_blind", nullable = false, length = 10)
    private String isBlind;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dt_id", nullable = false)
    private Debate dt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;

}