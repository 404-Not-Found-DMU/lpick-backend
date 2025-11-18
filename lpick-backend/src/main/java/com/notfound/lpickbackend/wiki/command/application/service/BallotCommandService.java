package com.notfound.lpickbackend.wiki.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.userinfo.command.repository.UserInfoCommandRepository;
import com.notfound.lpickbackend.wiki.command.application.domain.Ballot;
import com.notfound.lpickbackend.wiki.command.application.domain.Debate;
import com.notfound.lpickbackend.wiki.command.application.domain.DebateStatus;
import com.notfound.lpickbackend.wiki.query.dto.response.BallotResponse;
import com.notfound.lpickbackend.wiki.command.application.dto.request.BallotRequest;
import com.notfound.lpickbackend.wiki.command.repository.BallotCommandRepository;
import com.notfound.lpickbackend.wiki.command.repository.DebateCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BallotCommandService {

    private final BallotCommandRepository ballotCommandRepository;

    private final DebateCommandRepository debateCommandRepository;

    private final UserInfoCommandRepository userInfoCommandRepository;

    @Transactional
    public BallotResponse ballotToDebate(String debateId, BallotRequest req, OAuth2UserDetails userDetail) {

        // 이미 해당 토론에 투표했으면, 더이상 참여불가.
        if(ballotCommandRepository.existsByDebate_DtIdAndOauth_OauthId(debateId, userDetail.getUsername()))
            throw new CustomException(ErrorCode.ALREADY_BALLOT_IN_DEBATE);

        Debate targetDebate = debateCommandRepository.findById(debateId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DEBATE));
        
        // vote 상태가 아닌 토론에는 투표 불가능
        if(targetDebate.getIsEnd() == DebateStatus.OPEN)
            throw new CustomException(ErrorCode.DEBATE_STATUS_IS_OPEN);
        if(targetDebate.getIsEnd() == DebateStatus.CLOSE)
            throw new CustomException(ErrorCode.DEBATE_STATUS_IS_CLOSE);

        UserInfo targetUser = userInfoCommandRepository.findByOauthId(userDetail.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO));


        Ballot ballot = Ballot.builder()
                .debate(targetDebate)
                .oauth(targetUser)
                .ballotValue(req.getBallotValue())
                .build();

        Ballot resultBallot = ballotCommandRepository.save(ballot);

        return new BallotResponse(true, resultBallot.getBallotValue());
    }
}
