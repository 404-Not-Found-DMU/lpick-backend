package com.notfound.lpickbackend.wiki.command.application.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import com.notfound.lpickbackend.wiki.command.application.service.WikiLikeCommandService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class WikiLikeCommandController {
    private final WikiLikeCommandService wikiLikeCommandService;

    @PostMapping("/wiki/{wikiId}/like")
    @Operation(summary = "위키 좋아요 추가", description = "특정 위키에 대한 로그인 사용자의 좋아요 추가")
    public ResponseEntity<SuccessCode> createWikiLike(
            @PathVariable("wikiId") String wikiId,
            @AuthenticationPrincipal OAuth2UserDetails userDetail
    ) {
        wikiLikeCommandService.createWikiLike(wikiId, userDetail.getUsername());
        return ResponseEntity.ok(SuccessCode.LIKE_CREATE_SUCCESS);
    }

    @DeleteMapping("/wiki/{wikiId}/like")
    @Operation(summary = "위키 좋아요 삭제", description = "특정 위키에 대한 로그인 사용자의 좋아요 제거")
    public ResponseEntity<SuccessCode> deleteWikiLike(
            @PathVariable("wikiId") String wikiId,
            @AuthenticationPrincipal OAuth2UserDetails userDetail
    ) {
        wikiLikeCommandService.deleteWikiLike(wikiId, userDetail.getUsername());
        return ResponseEntity.ok(SuccessCode.DELETE_SUCCESS);
    }


}
