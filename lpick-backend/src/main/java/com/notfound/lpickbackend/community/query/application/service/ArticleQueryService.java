package com.notfound.lpickbackend.community.query.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.community.query.application.dto.ArticleDetailResponseDTO;
import com.notfound.lpickbackend.community.query.application.dto.ArticleListResponseDTO;
import com.notfound.lpickbackend.community.query.repository.ArticleBookmarkQueryRepository;
import com.notfound.lpickbackend.community.query.repository.ArticleLikeQueryRepository;
import com.notfound.lpickbackend.community.query.repository.ArticleQueryRepository;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleQueryService {

    private final ArticleQueryRepository articleQueryRepository;
    private final ArticleBookmarkQueryRepository articleBookmarkQueryRepository;
    private final ArticleLikeQueryRepository articleLikeQueryRepository;

    // 전체 게시글 목록 조회
    @Transactional(readOnly = true)
    public Page<ArticleListResponseDTO> readAllArticleList(Pageable pageable) {

        // 페이지 요청이 잘못 된 경우 예외 처리
        if (checkPageable(pageable)) {
            throw new CustomException(ErrorCode.INVALID_PAGE_REQUEST);
        }
        // 조회했을 때 게시글이 존재하지 않는 경우는 예외처리 하지 않음.
        return articleQueryRepository.findAllWithLikeAndCommentAndBookmarkCount(pageable);
    }

    // 게시글 상세 조회
    @Transactional(readOnly = true)
    public ArticleDetailResponseDTO readArticleDetail(String articleId) {

        ArticleDetailResponseDTO dto = articleQueryRepository.findByIdWithLikeAndCommentAndBookmarkCount(articleId);

        if(dto == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_ARTICLE);
        }

        String oAuthId = UserInfoUtil.getOAuthId();

        if(oAuthId.isEmpty()) { // 로그인 한 유저가 없을 경우
            dto.setLiked(false);
            dto.setBookmarked(false);

            return dto;
        }

        // 북마크, 좋아요 여부 확인
        if(isBookmarked(oAuthId, articleId)) dto.setBookmarked(true);
        if(isLiked(oAuthId, articleId)) dto.setLiked(true);

        return dto;
    }

    // 내가 작성한 게시글 목록 조회
    @Transactional(readOnly = true)
    public Page<ArticleListResponseDTO> readMyArticleList(Pageable pageable) {

        // 페이지 요청이 잘못 된 경우 예외 처리
        if (checkPageable(pageable)) {
            throw new CustomException(ErrorCode.INVALID_PAGE_REQUEST);
        }

        String oAuthId = UserInfoUtil.getOAuthId();
        if(oAuthId.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_PAGE_REQUEST);
        }

        return articleQueryRepository.findMyWithLikeAndCommentAndBookmarkCount(oAuthId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ArticleListResponseDTO> readMyLikedArticleList(Pageable pageable) {

        // 페이지 요청이 잘못 된 경우 예외 처리
        if (checkPageable(pageable)) {
            throw new CustomException(ErrorCode.INVALID_PAGE_REQUEST);
        }

        String oAuthId = UserInfoUtil.getOAuthId();
        if(oAuthId.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_PAGE_REQUEST);
        }

        return articleQueryRepository.findMyLikedWithLikeAndCommentAndBookmarkCount(oAuthId, pageable);
    }

    // 북마크 여부 확인
    private boolean isBookmarked (String oAuthId, String articleId) {
        return articleBookmarkQueryRepository.existsByOauthIdAndArticleId(oAuthId, articleId);
    }

    // 좋아요 여부 확인
    private boolean isLiked (String oAuthId, String articleId) {
        return articleLikeQueryRepository.existsByOauthIdAndArticleId(oAuthId, articleId);
    }

    private boolean checkPageable(Pageable pageable) {

        return pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0;
    }

}
