package com.notfound.lpickbackend.AUTO_ENTITIES;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

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

    @Column(name = "dt_id", nullable = false, length = 40)
    private String dtId;

    @Column(name = "oauth_id", nullable = false, length = 40)
    private String oauthId;

}