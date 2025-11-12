package com.notfound.lpickbackend.wiki.command.application.service;

import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.wiki.command.application.domain.Review;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wiki.command.application.dto.request.ReviewPostRequest;
import com.notfound.lpickbackend.wiki.command.repository.WikiReviewCommandRepository;
import com.notfound.lpickbackend.wiki.query.service.WikiPageQueryService;
import com.notfound.lpickbackend.wiki.query.service.WikiReviewQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WikiReviewCommandService {

    private final WikiPageQueryService wikiPageQueryService;

    private final WikiReviewQueryService wikiReviewQueryService;
    private final WikiReviewCommandRepository wikiReviewCommandRepository;

    @Transactional
    public void createNewReview(ReviewPostRequest req, String wikiId, UserInfo userInfo) {


        WikiPage wikiPage = wikiPageQueryService.getWikiPageById(wikiId);

        wikiReviewCommandRepository.save(Review.builder()
                .reviewId(null)
                .star(req.getStarScore())
                .content(req.getContent())
                .oauth(userInfo)
                .wiki(wikiPage).build()
        );

        throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);

    }

    public void updateReview(String reviewId, String userId, ReviewPostRequest req) {

        Review review = wikiReviewQueryService.findById(reviewId);

        // 본인 것 맞는지 검증 추가
        if (!wikiReviewCommandRepository.isEqualUser(userId, reviewId)) {
            throw new CustomException(ErrorCode.CAN_NOT_MODIFY_OTHER_USER_CONTENT);
        }

        review.updateReview(req);

        wikiReviewCommandRepository.save(review);


    }

    public void deleteById(String reviewId) {
        wikiReviewCommandRepository.deleteById(reviewId);
    }
}
