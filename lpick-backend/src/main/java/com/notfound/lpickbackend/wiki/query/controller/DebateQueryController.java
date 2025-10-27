package com.notfound.lpickbackend.wiki.query.controller;

import com.notfound.lpickbackend.wiki.query.dto.response.DebateHeader;
import com.notfound.lpickbackend.wiki.query.service.DebateQueryService;
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
@Tag(name = "토론 조회 컨트롤러", description = "토론 목록 조회 목적의 컨트롤러")
public class DebateQueryController {

    private final DebateQueryService debateQueryService;

    @GetMapping("/wiki/{wikiId}/debate")
    @Operation(summary = "토론 목록 조회", description = "토론 목록 조회. OPEN -> VOTE -> CLOSE 순으로 표기. 동일 상태 내에서는 최근 업데이트된 순서대로 표기.")
    public ResponseEntity<List<DebateHeader>> getDebateList(@PathVariable("wikiId") String wikiId) {
        return ResponseEntity.ok(debateQueryService.findDebateListByWikiId(wikiId));
    }
}
