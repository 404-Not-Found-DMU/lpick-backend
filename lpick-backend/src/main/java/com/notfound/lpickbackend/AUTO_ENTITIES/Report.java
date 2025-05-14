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
@Table(name = "report")
public class Report {
    @Id
    @Column(name = "report_id", nullable = false, length = 40)
    private String reportId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;

    @Column(name = "report_why", nullable = false, length = 50)
    private String reportWhy;

    @Column(name = "report_explain", nullable = false, length = Integer.MAX_VALUE)
    private String reportExplain;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}