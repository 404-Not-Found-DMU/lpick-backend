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

    // 현재로서는, id 값을 알아야 접근 가능.
    // 검색창에 wikiPage가 지닌 title 기입 시 유사한 목록 제공하거나, 특정 대상 wikiPage 제공 필요
    // 동일한 이름의 문서가 존재할 수 있다. 이 경우 인지도 기반으로 먼저 제공될 수 있도록 구현?
    @GetMapping("/wiki/{wikiId}")
    public ResponseEntity<WikiPageViewResponse> getWikiPageView(
            @PathVariable("wikiId")String wikiId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(wikiPageQueryService.getWikiPageView(wikiId));
    }


}
