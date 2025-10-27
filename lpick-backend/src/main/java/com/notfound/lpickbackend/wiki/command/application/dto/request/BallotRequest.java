package com.notfound.lpickbackend.wiki.command.application.dto.request;

import com.notfound.lpickbackend.wiki.command.application.domain.BallotValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BallotRequest {
    private BallotValue ballotValue;
}
