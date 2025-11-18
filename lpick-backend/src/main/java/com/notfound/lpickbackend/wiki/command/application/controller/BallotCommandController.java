package com.notfound.lpickbackend.wiki.command.application.controller;

import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import com.notfound.lpickbackend.wiki.query.dto.response.BallotResponse;
import com.notfound.lpickbackend.wiki.command.application.dto.request.BallotRequest;
import com.notfound.lpickbackend.wiki.command.application.service.BallotCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "토론 투표 컨트롤러", description = "토론에 대한 투표 목적의 컨트롤러")
public class BallotCommandController {

    private final BallotCommandService ballotCommandService;


    @Operation(summary = "토론 투표", description = "로그인 필요. status가 VOTE인 상태인 debate에만 투표가능합니다. BallotValue는 다음과 같아야합니다 - AGREE, DISAGREE, ABSTAIN")
    @PostMapping("/debate/{debateId}/ballot")
    public ResponseEntity<BallotResponse> ballotInDebate(
            @PathVariable("debateId") String debateId,
            @RequestBody BallotRequest req,
            @AuthenticationPrincipal OAuth2UserDetails userDetail
    ) {

        return ResponseEntity.ok(ballotCommandService.ballotToDebate(debateId, req, userDetail));
    }
}
