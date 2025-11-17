package com.notfound.lpickbackend.community.query.service;

import com.notfound.lpickbackend.community.query.dto.CommentListResponse;
import com.notfound.lpickbackend.community.query.dto.ParentsCommentResponse;
import com.notfound.lpickbackend.community.query.repository.CommentQueryRepository;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.servicedata.query.inherenceEnum.CommentListFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentQueryService {
    private final CommentQueryRepository commentQueryRepository;

    // 게시글의 댓글 목록 조회
    public Page<ParentsCommentResponse> readCommentList(
            String articleId,
            Pageable pageable
    ) {
        String oauthId = UserInfoUtil.getOAuthId();

        return commentQueryRepository.findCommentsWithChildrenAndLikes(articleId, oauthId, pageable);
    }

    // 내가 좋아요 누른 답글 조회
    public Page<ParentsCommentResponse> readLikedParentsCommentList(Pageable pageable) {

        String oauthId = UserInfoUtil.getOAuthId();

        return commentQueryRepository.findParentsByOauthIdAndCommentLike(oauthId, pageable);
    }

    // 내가 좋아요 누른 답글 조회
    public Page<ParentsCommentResponse> readLikedChildCommentList(Pageable pageable) {

        String oauthId = UserInfoUtil.getOAuthId();

        return commentQueryRepository.findChildByOauthIdAndCommentLike(oauthId, pageable);
    }

    public int countCommentByOauthId(String oauthId) {
        return commentQueryRepository.countByOauth_OauthId(oauthId);
    }

    public Page<CommentListResponse> readMyCommentList(String oauthId, CommentListFilter filter, Pageable pageable) {

        return commentQueryRepository.findCommentListByOauthIdWithFilter(oauthId, filter, pageable);
    }
}
