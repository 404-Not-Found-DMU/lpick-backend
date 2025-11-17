package com.notfound.lpickbackend.wiki.query.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import com.notfound.lpickbackend.wiki.query.dto.response.WikiLikeResponse;
import com.notfound.lpickbackend.wiki.query.service.WikiLikeQueryService;
import io.swagger.v3.oas.annotations.Operation;
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
public class WikiLikeQueryController {
    private final WikiLikeQueryService wikiLikeQueryService;


    @GetMapping("/wiki/{wikiId}/like")
    @Operation(summary = "위키 좋아요 추가", description = "특정 위키에 대한 로그인 사용자의 좋아요 추가")
    public ResponseEntity<WikiLikeResponse> createWikiLike(
            @PathVariable("wikiId") String wikiId,
            @AuthenticationPrincipal OAuth2UserDetails userDetail
    ) {
        return ResponseEntity.ok(wikiLikeQueryService.getWikiLikeByWikiId(wikiId, userDetail.getUsername()));
    }
}
