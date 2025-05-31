package com.notfound.lpickbackend.wiki.query.controller;

import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import com.notfound.lpickbackend.userInfo.query.service.UserInfoQueryService;
import com.notfound.lpickbackend.wiki.query.dto.response.WikiBookmarkResponse;
import com.notfound.lpickbackend.wiki.query.dto.response.WikiPageBookmarkListResponse;
import com.notfound.lpickbackend.wiki.query.service.WikiBookmarkQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WikiBookmarkQueryController {

    private final WikiBookmarkQueryService wikiBookmarkQueryService;
    private final UserInfoQueryService userInfoQueryService;

    @GetMapping("/wiki/bookmark-list")
    public ResponseEntity<Page<WikiPageBookmarkListResponse>> getWikiBookmarkList(
            @RequestParam("dummyUserId") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UserInfo userInfo = userInfoQueryService.getUserInfoById(userId);

        Page<WikiPageBookmarkListResponse> bookmarkList = wikiBookmarkQueryService.getWikiBookmarkListByUserId(PageRequest.of(page, size), userInfo);

        return ResponseEntity.ok().body(bookmarkList);
    }

    /** 현재 페이지에 대한 북마크 검증 - 프론트 측 상태 관리되지 않은경우 업데이트 참조 목적 */
    @GetMapping("/wiki/{wikiId}/bookmark-status")
    public ResponseEntity<Boolean> checkWikiBookmarkStatus(
            @PathVariable("wikiId") String wikiId,
            @RequestParam("dummyUserId") String userId
    ) {

        UserInfo userInfo = userInfoQueryService.getUserInfoById(userId);

        Boolean isExistsBookmark = wikiBookmarkQueryService.existsByWiki_WikiIdAndOauth_oauthId(wikiId, userInfo.getOauthId());

        return ResponseEntity.ok().body(isExistsBookmark);

    }
}
