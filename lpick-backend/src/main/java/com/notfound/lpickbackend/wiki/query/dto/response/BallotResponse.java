package com.notfound.lpickbackend.wiki.query.dto.response;

import com.notfound.lpickbackend.wiki.command.application.domain.BallotValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BallotResponse {
    private boolean isVoted; // 현재 투표 했는지
    private BallotValue votedBallotValue; // 투표한 결과
}
