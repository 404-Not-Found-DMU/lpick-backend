package com.notfound.lpickbackend.wiki.query.controller;

import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.userinfo.query.service.UserInfoQueryService;
import com.notfound.lpickbackend.wiki.query.dto.response.WikiBookmarkResponse;
import com.notfound.lpickbackend.wiki.query.dto.response.WikiPageBookmarkListResponse;
import com.notfound.lpickbackend.wiki.query.service.WikiBookmarkQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated // RequestParam의 Pattern을 동작시키기 위한 필수 어노테이션
@Tag(name = "위키 북마크 조회 컨트롤러", description = "위키 북마크 관련 조회 기능 컨트롤러")
public class WikiBookmarkQueryController {

    private final WikiBookmarkQueryService wikiBookmarkQueryService;
    private final UserInfoQueryService userInfoQueryService;


    // userInfo 쪽 구현 시 그쪽으로 이전해야할 요청입니다. 사용자가 지니는 위키 북마크 목록을 가져오는 요청.
    // 위치를 잘못구현했는데 일단 남겨둡니다.
    @GetMapping("/wiki/book-mark-list")
    @Operation(summary = "북마크 목록 조회", description = "사용자의 북마크 목록을 조회하는 기능")
    public ResponseEntity<Page<WikiPageBookmarkListResponse>> getWikiBookmarkList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "class", required = false) // 필수가 아니도록 명시
            @Pattern(
                    regexp = "^(artist|gear|album|other)$", // 문자열 시작과 끝 명확히 들어오도록 설계
                    message = "class는 artist, gear, album, other 중 하나여야합니다."
            )
            String targetClass
    ) {
        Page<WikiPageBookmarkListResponse> bookmarkList = wikiBookmarkQueryService.getWikiBookmarkListByOauthId(PageRequest.of(page, size), targetClass);

        return ResponseEntity.ok().body(bookmarkList);
    }

    /** 현재 위키 페이지에 대한 사용자의 북마크 존재여부 검증 - 프론트 측 상태 관리되지 않은경우 업데이트 참조 목적 */
    @GetMapping("/wiki/{wikiId}/bookmark")
    @Operation(summary = "북마크 여부 확인", description = "특정 위키에 대한 북마크 여부 확인")
    public ResponseEntity<WikiBookmarkResponse> checkWikiBookmarkStatus(
            @PathVariable("wikiId") String wikiId,
            @RequestParam("dummyUserId") String userId
    ) {


        UserInfo userInfo = userInfoQueryService.getUserInfoById(userId);

        WikiBookmarkResponse bookmarkResponse = wikiBookmarkQueryService.findByWikiIdAndOauthId(wikiId, userInfo.getOauthId());

        return ResponseEntity.ok().body(bookmarkResponse);

    }
}
