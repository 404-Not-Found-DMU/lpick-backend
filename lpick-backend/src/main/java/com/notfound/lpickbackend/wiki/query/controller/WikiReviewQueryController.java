package com.notfound.lpickbackend.wiki.query.controller;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.SuccessCode;
import com.notfound.lpickbackend.wiki.query.dto.response.ReviewResponse;
import com.notfound.lpickbackend.wiki.query.service.WikiReviewQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WikiReviewQueryController {

    private final WikiReviewQueryService wikiReviewQueryService;

    @GetMapping("/wiki/{wikiId}/review")
    public ResponseEntity<Page<ReviewResponse>> getReviewListInWiki(
            @PathVariable("wikiId") String wikiId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ReviewResponse> reviewList = wikiReviewQueryService.getReviewResponseListInWiki(PageRequest.of(page, size), wikiId);

        return ResponseEntity.status(HttpStatus.OK).body(reviewList);
    }



}
