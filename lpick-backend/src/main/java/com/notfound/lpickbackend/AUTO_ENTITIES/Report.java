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
@Table(name = "report")
public class Report {
    @Id
    @Column(name = "report_id", nullable = false, length = 40)
    private String reportId;

    @Column(name = "oauth_id", nullable = false, length = 40)
    private String oauthId;

    @Column(name = "report_why", nullable = false, length = 50)
    private String reportWhy;

    @Column(name = "report_explain", nullable = false, length = Integer.MAX_VALUE)
    private String reportExplain;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}