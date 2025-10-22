package com.notfound.lpickbackend.wiki.command.application.controller;

import com.notfound.lpickbackend.wiki.command.application.service.DebateCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DebateCommandController {
    private final DebateCommandService debateCommandService;

    private final SimpMessagingTemplate template;

    @PostMapping("/api/v1/dev/broadcast/{roomId}")
    public void push(@PathVariable String roomId, @RequestBody Map<String, Object> body) {
        template.convertAndSend("/topic/rooms/" + roomId, body);
    }

}
