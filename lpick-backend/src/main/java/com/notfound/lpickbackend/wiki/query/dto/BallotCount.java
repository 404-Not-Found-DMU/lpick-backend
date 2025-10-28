package com.notfound.lpickbackend.wiki.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 리포지토리 집계용 */
@Getter
@AllArgsConstructor
public class BallotCount {
    private Long agree;
    private Long disagree;
    private Long abstain;
    private Long total;
}