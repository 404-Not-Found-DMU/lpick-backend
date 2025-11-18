package com.notfound.lpickbackend.wiki.query.controller;

import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import com.notfound.lpickbackend.wiki.query.dto.response.BallotResponse;
import com.notfound.lpickbackend.wiki.query.dto.response.DebateBallotStatResponse;
import com.notfound.lpickbackend.wiki.query.service.BallotQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "토론 투표 조회 컨트롤러", description = "토론에 대한 투표 조회 목적의 컨트롤러")
public class BallotQueryController {

    private final BallotQueryService ballotQueryService;    @GetMapping("/debate/{debateId}/ballot")
    @Operation(summary = "토론 투표 상황 및 결과 조회", description = "진행중이거나 종료된 투표 모두 본 요청을 이용해 확인. 해당 토론의 현재 status가 open인 경우 확인할 수 없습니다.  AGREE(찬성), DISAGREE(반대), ABSTAIN(기권, 중립).")
    public ResponseEntity<DebateBallotStatResponse> countBallotResult(
            @PathVariable("debateId") String debateId
    ) {

        return ResponseEntity.ok(ballotQueryService.getBallotStat(debateId));
    }



    @GetMapping("/debate/{debateId}/ballot/history")
    @Operation(summary = "특정 토론 내 본인의 투표여부 및 내역 확인", description = "[사용자] 로그인한 인원이 특정 토론 내에 투표를 했는지, 어떤 투표를 했는지 확인 가능합니다. AGREE(찬성), DISAGREE(반대), ABSTAIN(기권, 중립).")
    public ResponseEntity<BallotResponse> getBallotHistory(
            @PathVariable("debateId") String debateId,
            @AuthenticationPrincipal OAuth2UserDetails userDetail
            ) {

        return ResponseEntity.ok(ballotQueryService.getUserBallotValue(userDetail.getUsername(), debateId));
    }
}
