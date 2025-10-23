package com.notfound.lpickbackend.wiki.command.application.controller;

import com.notfound.lpickbackend.common._wrapper.IdResponse;
import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import com.notfound.lpickbackend.wiki.command.application.service.DebateCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DebateCommandController {
    private final DebateCommandService debateCommandService;

    private final SimpMessagingTemplate template;

    @PostMapping("/dev/broadcast/{roomId}")
    public void push(@PathVariable String roomId, @RequestBody Map<String, Object> body) {
        template.convertAndSend("/topic/rooms/" + roomId, body);
    }

    @PostMapping("/wiki/{wikiId}/debate")
    public ResponseEntity<IdResponse<SuccessCode>> createDebate(
            @PathVariable("wikiId") String wikiId,
            @AuthenticationPrincipal OAuth2UserDetails userDetail

    ) {
        return ResponseEntity.ok(new IdResponse<>(debateCommandService.createDebate(wikiId, userDetail), SuccessCode.CREATE_SUCCESS));
    }

}
