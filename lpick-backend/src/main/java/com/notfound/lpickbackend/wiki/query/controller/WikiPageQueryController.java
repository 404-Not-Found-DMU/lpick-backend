package com.notfound.lpickbackend.wiki.query.controller;

import com.notfound.lpickbackend.wiki.query.dto.response.WikiPageViewResponse;
import com.notfound.lpickbackend.wiki.query.service.WikiPageQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WikiPageQueryController {
    private final WikiPageQueryService wikiPageQueryService;

    @GetMapping("/wiki/{wikiId}")
    public ResponseEntity<WikiPageViewResponse> getWikiPageView(
            @PathVariable("wikiId")String wikiId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(wikiPageQueryService.getWikiPageView(wikiId));
    }


}
