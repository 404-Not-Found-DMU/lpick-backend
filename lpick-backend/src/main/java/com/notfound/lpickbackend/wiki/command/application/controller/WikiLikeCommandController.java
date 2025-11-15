package com.notfound.lpickbackend.wiki.command.application.controller;

import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import com.notfound.lpickbackend.wiki.command.application.service.WikiLikeCommandService;
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
    public ResponseEntity<SuccessCode> createWikiLike(
            @PathVariable("wikiId") String wikiId,
            @AuthenticationPrincipal OAuth2UserDetails userDetail
    ) {
        wikiLikeCommandService.createWikiLike(wikiId, userDetail.getUsername());
        return ResponseEntity.ok(SuccessCode.LIKE_CREATE_SUCCESS);
    }

    @DeleteMapping("/wiki/{wikiId}/like")
    public ResponseEntity<SuccessCode> deleteWikiLike(
            @PathVariable("wikiId") String wikiId,
            @AuthenticationPrincipal OAuth2UserDetails userDetail
    ) {
        wikiLikeCommandService.deleteWikiLike(wikiId, userDetail.getUsername());
        return ResponseEntity.ok(SuccessCode.DELETE_SUCCESS);
    }


}
