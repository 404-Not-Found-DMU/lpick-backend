package com.notfound.lpickbackend.wiki.command.application.controller;

import com.notfound.lpickbackend.common._wrapper.IdResponse;
import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import com.notfound.lpickbackend.wiki.command.application.domain.DebateStatus;
import com.notfound.lpickbackend.wiki.command.application.dto.request.DebateRequest;
import com.notfound.lpickbackend.wiki.command.application.service.DebateCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "토론 컨트롤러", description = "토론생성 및 상태수정 목적의 컨트롤러")
public class DebateCommandController {
    private final DebateCommandService debateCommandService;

    private final SimpMessagingTemplate template;

//    @PostMapping("/dev/broadcast/{roomId}")
//    public void push(@PathVariable String roomId, @RequestBody Map<String, Object> body) {
//        template.convertAndSend("/topic/rooms/" + roomId, body);
//    }

    @PostMapping("/wiki/{wikiId}/debate")
    @Operation(summary = "토론 생성", description = "로그인 필요. 대상 위키문서에 대한 토론 생성. 목적이 되는 버전의 리비전은 requestBody로 전달받음. 생성시 OPEN status로 생성됨. 토론생성 목적(subject)은 - REPRESENTATION(표기), DETAIL(내용)")
    public ResponseEntity<IdResponse<SuccessCode>> createDebate(
            @RequestBody DebateRequest req,
            @PathVariable("wikiId") String wikiId,
            @AuthenticationPrincipal OAuth2UserDetails userDetail

    ) {
        return ResponseEntity.ok(new IdResponse<>(debateCommandService.createDebate(req, wikiId, userDetail), SuccessCode.CREATE_SUCCESS));
    }

    // 어드민만 호출 가능
    @PatchMapping("/debate/{debateId}")
    @Operation(summary = "토론 상태 수정", description = "대상 토론 상태를 투표 또는 종료로 수정. Status Enum - VOTE(투표단계), CLOSE(종료)")
    public ResponseEntity<SuccessCode> patchDebateStatus(
            @PathVariable("debateId") String debateId,
            @RequestParam("status") DebateStatus status
//            @AuthenticationPrincipal OAuth2UserDetails userDetail // 본 방식 또는 PreAuthorize?로 jwt 뜯어서 role 확인 예정

    ) {
        debateCommandService.patchDebateStatus(debateId, status);
        return ResponseEntity.ok(SuccessCode.NO_CONTENT);
    }

}
