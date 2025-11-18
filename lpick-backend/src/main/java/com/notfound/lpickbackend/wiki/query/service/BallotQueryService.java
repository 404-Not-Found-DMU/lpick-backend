package com.notfound.lpickbackend.wiki.query.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.debate.query.repository.DebateQueryRepository;
import com.notfound.lpickbackend.wiki.command.application.domain.Ballot;
import com.notfound.lpickbackend.wiki.command.application.domain.Debate;
import com.notfound.lpickbackend.wiki.command.application.domain.DebateStatus;
import com.notfound.lpickbackend.wiki.query.dto.response.BallotResponse;
import com.notfound.lpickbackend.wiki.query.dto.BallotCount;
import com.notfound.lpickbackend.wiki.query.dto.response.DebateBallotStatResponse;
import com.notfound.lpickbackend.wiki.query.repository.BallotQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BallotQueryService {

    private final DebateQueryRepository debateQueryRepository;

    private final BallotQueryRepository ballotQueryRepository;
    @Transactional(readOnly = true)
    public DebateBallotStatResponse getBallotStat(String dtId) {

        Debate targetDebate = debateQueryRepository.findById(dtId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DEBATE));

        if(targetDebate.getIsEnd() == DebateStatus.OPEN) throw new CustomException(ErrorCode.DEBATE_STATUS_IS_OPEN);

        BallotCount c = ballotQueryRepository.countByDebateGrouped(dtId);
        if (c == null) {
            // debate가 존재하지만 투표가 전무한 경우에도 JPQL은 한 행을 반환하므로 보통 null이 아니지만,
            // 방어적으로 0 세팅
            c = new BallotCount(0L, 0L, 0L, 0L);
        }

        long agree    = nz(c.getAgree());
        long disagree = nz(c.getDisagree());
        long abstain  = nz(c.getAbstain());
        long total    = nz(c.getTotal());

        return DebateBallotStatResponse.builder()
                .debateId(dtId)
                .total(total)
                .agree(agree)
                .disagree(disagree)
                .abstain(abstain)
                .agreePct(pct(agree, total))
                .disagreePct(pct(disagree, total))
                .abstainPct(pct(abstain, total))
                .build();
    }

    private static long nz(Long v) { return v == null ? 0L : v; }

    // 소수 1자리 반올림(원하면 2자리로 변경)
    private static double pct(long part, long total) {
        if (total <= 0) return 0.0;
        double raw = (part * 100.0) / total;
        return Math.round(raw * 10.0) / 10.0;
    }

    public BallotResponse getUserBallotValue(String oauthId, String debateId) {

        BallotResponse response = new BallotResponse(false, null);
        Optional<Ballot> ballotOptional = ballotQueryRepository.findByOauth_OauthIdAndDebate_DtId(oauthId, debateId);

        if(ballotOptional.isPresent()) {
            response = new BallotResponse(true, ballotOptional.get().getBallotValue());
        }

        return response;
    }
}