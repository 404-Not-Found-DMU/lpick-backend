package com.notfound.lpickbackend.debate.query;

import com.notfound.lpickbackend.debate.query.service.DebateChatQueryService;
import com.notfound.lpickbackend.wiki.query.dto.DebateChatInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "토론 채팅 조회 컨트롤러", description = "토론 채팅 메시지 목록 조회 목적의 컨트롤러")
public class DebateChatQueryController {

    private final DebateChatQueryService debateChatQueryService;

    @GetMapping("/debate/{debateId}/chat-list")
    @Operation(summary = "토론 채팅 조회", description = "토론 채팅 메시지 목록을 조회. createdAt 기준으로 오름차순 정렬. 요청 시점까지의 채팅 목록을 불러옵니다. 실시간 업데이트는 WebSocket 연결망 사용 요망.")
    public ResponseEntity<List<DebateChatInfo>> getChatMessageList(@PathVariable String debateId) {
        return ResponseEntity.ok(debateChatQueryService.readDebateChatList(debateId));
    }


}
