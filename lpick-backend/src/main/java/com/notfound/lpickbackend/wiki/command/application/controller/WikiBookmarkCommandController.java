package com.notfound.lpickbackend.wiki.command.application.controller;

import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.userInfo.query.service.UserInfoQueryService;
import com.notfound.lpickbackend.wiki.command.application.service.WikiBookmarkCommandService;
import com.notfound.lpickbackend.wiki.query.service.WikiBookmarkQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WikiBookmarkCommandController {

    private final WikiBookmarkCommandService wikiBookmarkCommandService;
    private final WikiBookmarkQueryService wikiBookmarkQueryService;

    private final UserInfoQueryService userInfoQueryService;
    /** bookmark(북마크), thumbsup(좋아요) 기능에 대한 설계 설정*/
    // 1. 프론트가 별도 상태관리 없이 PATCH 요청 보내면 백엔드 측에서 구독상태 확인하여 sub/unsub
    // 2. 프론트에서 상태관리 + 백엔드는 sub/unsub 각각 POST / DELETE 기반 구현하면 프론트가 현재 상태따라 요청 전달
    // 2번 방식으로 진행. 사유 1. RESTful대로 구현 위해 2번 방식 사용 필요
    // 사유 2. 어차피 프론트에서 상태에 따라 표기해주는 코드 들어가지 않나..?

    @PostMapping("/wiki/{wikiId}/bookmark")
    public ResponseEntity<SuccessCode> subscribeWikiBookmark(
            @PathVariable("wikiId") String wikiId,
            @RequestParam("dummyUserId") String userId
    ) {
        UserInfo userInfo = userInfoQueryService.getUserInfoById(userId); // security 기반 코드와 병합 시 수정 예정

        // 해당 요청은 프론트 상태를 기반으로 POST 또는 DELETE를 받아옴.
        // 프론트 상태 업데이트 되지 않은 상태로 중복 요청 들어올 수 있으므로
        // 이를 Service 단에서 별도로 체크하여 중복 요청은 무시할 수 있도록 구현하기.
        if(wikiBookmarkQueryService.existsByWiki_WikiIdAndOauth_oauthId(wikiId, userInfo.getOauthId())) {
            // 204 Not Content로 구현해 단순히 넘기기 Vs 409 Conflict로 구현해 프론트측에 에러 명시처리해 GET 통한 업데이트 유도 
            throw new CustomException(ErrorCode.DO_NOT_KEEP_UP_THIS_ERROR_WHEN_MERGE);
        }

        wikiBookmarkCommandService.createNewWikiBookmark(wikiId, userInfo);

        return ResponseEntity.ok(SuccessCode.CREATE_SUCCESS);
    }

    // wikiBookmark는 Update 할일 없는 대상이므로 제외.

    @DeleteMapping("/wiki/{wikiId}/bookmark")
    public ResponseEntity<SuccessCode> unsubscribeWikiBookmark(
            @PathVariable("wikiId") String wikiId,
            @RequestParam("dummyUserId") String userId
    ) {
        UserInfo userInfo = userInfoQueryService.getUserInfoById(userId); // security 기반 코드와 병합 시 수정 예정

        // 해당 요청은 프론트 상태를 기반으로 POST 또는 DELETE를 받아옴.
        // 프론트 상태 업데이트 되지 않은 상태로 중복 요청 들어올 수 있으므로
        // 이를 Service 단에서 별도로 체크하여 중복 요청은 무시할 수 있도록 구현하기.
        if(wikiBookmarkQueryService.existsByWiki_WikiIdAndOauth_oauthId(wikiId, userInfo.getOauthId())) {
            // 204 No Content로 구현해 단순히 넘기기 Vs 404 Not Found로 구현해 프론트 측에 에러 명시처리로 GET 통한 업데이트 유도
            throw new CustomException(ErrorCode.DO_NOT_KEEP_UP_THIS_ERROR_WHEN_MERGE);
        }

        wikiBookmarkCommandService.deleteWikiBookmark(wikiId, userInfo);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }
}
