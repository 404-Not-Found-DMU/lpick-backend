package com.notfound.lpickbackend.AUTO_ENTITIES;

import com.notfound.lpickbackend.wikipage.command.application.domain.WikiPage;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

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