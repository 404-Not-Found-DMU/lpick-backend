package com.notfound.lpickbackend.wiki.query.controller;

import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import com.notfound.lpickbackend.userinfo.query.service.UserInfoQueryService;
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

@RestController
@RequiredArgsConstructor
public class WikiBookmarkQueryController {

    private final WikiBookmarkQueryService wikiBookmarkQueryService;
    private final UserInfoQueryService userInfoQueryService;


    // userInfo 쪽 구현 시 그쪽으로 이전해야할 요청입니다. 사용자가 지니는 위키 북마크 목록을 가져오는 요청.
    // 위치를 잘못구현했는데 일단 남겨둡니다.
    @GetMapping("/wiki/book-mark-list")
    public ResponseEntity<Page<WikiPageBookmarkListResponse>> getWikiBookmarkList(
            @RequestParam("dummyUserId") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UserInfo userInfo = userInfoQueryService.getUserInfoById(userId);

        Page<WikiPageBookmarkListResponse> bookmarkList = wikiBookmarkQueryService.getWikiBookmarkListByUserId(PageRequest.of(page, size), userInfo);

        return ResponseEntity.ok().body(bookmarkList);
    }

    /** 현재 위키 페이지에 대한 사용자의 북마크 존재여부 검증 - 프론트 측 상태 관리되지 않은경우 업데이트 참조 목적 */
    @GetMapping("/wiki/{wikiId}/bookmark")
    public ResponseEntity<WikiBookmarkResponse> checkWikiBookmarkStatus(
            @PathVariable("wikiId") String wikiId,
            @RequestParam("dummyUserId") String userId
    ) {


        UserInfo userInfo = userInfoQueryService.getUserInfoById(userId);

        WikiBookmarkResponse bookmarkResponse = wikiBookmarkQueryService.findByWikiIdAndOauthId(wikiId, userInfo.getOauthId());

        return ResponseEntity.ok().body(bookmarkResponse);

    }
}
