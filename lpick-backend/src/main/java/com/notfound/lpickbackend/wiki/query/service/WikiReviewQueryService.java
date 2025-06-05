package com.notfound.lpickbackend.wiki.query.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.wiki.command.application.domain.Review;
import com.notfound.lpickbackend.wiki.query.dto.response.ReviewResponse;
import com.notfound.lpickbackend.wiki.query.repository.WikiReviewQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WikiReviewQueryService {

    private final WikiReviewQueryRepository wikiReviewQueryRepository;

    public Page<ReviewResponse> getReviewResponseListInWiki(Pageable pageable, String wikiId) {

        return wikiReviewQueryRepository.findAllByWiki_wikiId(wikiId, pageable)
                .map(review -> {
                    return ReviewResponse.builder()
                        .reviewId(review.getReviewId())
                        .content(review.getContent())
                        .starScore(review.getStar())
                        .createdAt(review.getCreatedAt())
                        .build();
                });

    }


    public boolean isAlreadyExsisttWikiReview(String wikiId, String oauthId) {
        return wikiReviewQueryRepository.existsByWiki_wikiIdAndOauth_oauthId(wikiId, oauthId);
    }

    public Review findById(String reviewId) {
        return wikiReviewQueryRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));
    }

    public Review findByWikiIdAndUserId(String wikiId, String userId) {
        return wikiReviewQueryRepository.findByWiki_wikiIdAndOauth_oauthId(wikiId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));
    }
}
