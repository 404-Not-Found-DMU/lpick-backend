package com.notfound.lpickbackend.wiki.command.application.controller;

import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.userInfo.query.service.UserInfoQueryService;
import com.notfound.lpickbackend.wiki.command.application.dto.request.ReviewPostRequest;
import com.notfound.lpickbackend.wiki.command.application.service.WikiReviewCommandService;
import com.notfound.lpickbackend.wiki.query.service.WikiPageQueryService;
import com.notfound.lpickbackend.wiki.query.service.WikiReviewQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WikiReviewCommandController {
    
    private final UserInfoQueryService userInfoQueryService;
    private final WikiPageQueryService wikiPageQueryService;
    
    private final WikiReviewCommandService wikiReviewCommandService;
    private final WikiReviewQueryService wikiReviewQueryService;



    @PostMapping("/wiki/{wikiId}/review")
    public ResponseEntity<SuccessCode> writeReview(
            @PathVariable("wikiId") String wikiId,
            @RequestParam("userId") String userId,
            @RequestBody @Valid ReviewPostRequest req,
            @RequestParam("sort") String sortBy
    ) {
        UserInfo userInfo = userInfoQueryService.getUserInfoById(userId); // security 추가후 리팩

        if(wikiReviewQueryService.isAlreadyExsisttWikiReview(wikiId, userId))
            throw new CustomException(ErrorCode.ALREADY_HAS_REVIEW_IN_WIKIPAGE);

        wikiReviewCommandService.createNewReview(req, wikiId, userInfo);

        return ResponseEntity.ok(SuccessCode.CREATE_SUCCESS);
    }

    // 리뷰는 수정 없이 작성/삭제만 가능.. 으로 기억합니다.

    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<SuccessCode> deleteReview(
            @PathVariable("reviewId") String reviewId,
            @RequestParam("userId") String userId
    ) {
        UserInfo userInfo = userInfoQueryService.getUserInfoById(userId);

        wikiReviewCommandService.deleteById(reviewId);

        return ResponseEntity.ok(SuccessCode.SUCCESS);
    }



}
