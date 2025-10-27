package com.notfound.lpickbackend.wiki.query.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebateBallotStatResponse {
    private String debateId;
    private long total;

    private long agree;
    private long disagree;
    private long abstain;

    private double agreePct;     // 0.0 ~ 100.0
    private double disagreePct;
    private double abstainPct;
}