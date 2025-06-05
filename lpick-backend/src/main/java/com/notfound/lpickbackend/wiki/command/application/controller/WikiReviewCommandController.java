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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WikiReviewCommandController {
    
    private final UserInfoQueryService userInfoQueryService;
    private final WikiPageQueryService wikiPageQueryService;
    
    private final WikiReviewCommandService wikiReviewCommandService;
    private final WikiReviewQueryService wikiReviewQueryService;



    @PostMapping("/wiki/{wikiId}/review")
    //@PreAuthorize("hasRole('TIER_SILVER')") // 임시 설정
    public ResponseEntity<SuccessCode> writeReview(
            @PathVariable("wikiId") String wikiId,
            @RequestParam("userId") String userId,
            @RequestBody @Valid ReviewPostRequest req
    ) {
        UserInfo userInfo = userInfoQueryService.getUserInfoById(userId); // security 추가후 리팩

        if(wikiReviewQueryService.isAlreadyExsisttWikiReview(wikiId, userId))
            throw new CustomException(ErrorCode.ALREADY_HAS_REVIEW_IN_WIKIPAGE);

        wikiReviewCommandService.createNewReview(req, wikiId, userInfo);

        return ResponseEntity.ok(SuccessCode.CREATE_SUCCESS);
    }

    @PatchMapping("/review/{reviewId}")
    //@PreAuthorize("hasRole('TIER_SILVER')") // 임시 설정
    public ResponseEntity<SuccessCode> updateReview(
            @PathVariable("reviewId") String reviewId,
            @RequestBody @Valid ReviewPostRequest req
    ) {
        wikiReviewCommandService.updateReview(reviewId,req);

        return ResponseEntity.ok(SuccessCode.NO_CONTENT);
    }

    @DeleteMapping("/review/{reviewId}")
    //@PreAuthorize("hasRole('TIER_SILVER')") // 임시 설정
    public ResponseEntity<SuccessCode> deleteReview(
            @PathVariable("reviewId") String reviewId,
            @RequestParam("userId") String userId
    ) {
        UserInfo userInfo = userInfoQueryService.getUserInfoById(userId);

        wikiReviewCommandService.deleteById(reviewId);

        return ResponseEntity.ok(SuccessCode.NO_CONTENT);
    }



}
