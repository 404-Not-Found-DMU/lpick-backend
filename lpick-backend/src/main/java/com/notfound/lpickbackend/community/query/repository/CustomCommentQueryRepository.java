package com.notfound.lpickbackend.community.query.repository;

import com.notfound.lpickbackend.community.query.dto.CommentListResponse;
import com.notfound.lpickbackend.community.query.dto.ParentsCommentResponse;
import com.notfound.lpickbackend.servicedata.query.inherenceEnum.CommentListFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// QueryDSL 사용을 위한 커스텀 클래스(JpaRepository에선 쿼리메소드 작성 못함)
public interface CustomCommentQueryRepository {

    Page<ParentsCommentResponse> findCommentsWithChildrenAndLikes(
            String articleId,
            String oauthId,
            Pageable pageable
    );

    Page<ParentsCommentResponse> findParentsByOauthIdAndCommentLike(
            String oAuthId,
            Pageable pageable
    );

    Page<ParentsCommentResponse> findChildByOauthIdAndCommentLike(
            String oAuthId,
            Pageable pageable
    );

    // 내가 쓴 댓글 목록 + 필터
    Page<CommentListResponse> findCommentListByOauthIdWithFilter(
            String oauthId,
            CommentListFilter filter,
            Pageable pageable
    );
}
