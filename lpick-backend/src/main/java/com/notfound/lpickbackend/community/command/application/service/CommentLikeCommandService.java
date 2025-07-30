package com.notfound.lpickbackend.community.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.community.command.domain.Comment;
import com.notfound.lpickbackend.community.command.domain.CommentLike;
import com.notfound.lpickbackend.community.command.repository.CommentLikeCommandRepository;
import com.notfound.lpickbackend.community.query.repository.CommentQueryRepository;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import com.notfound.lpickbackend.userinfo.query.repository.UserInfoQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeCommandService {

    private final CommentLikeCommandRepository commentLikeCommandRepository;
    private final CommentQueryRepository commentQueryRepository;
    private final UserInfoQueryRepository userInfoQueryRepository;

    // 댓글 좋아요 추가
    public void createCommentLike(String commentId) {

        Comment comment = getComment(commentId);
        UserInfo userInfo = getUserInfo();

        CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .oauth(userInfo)
                .build();

        commentLikeCommandRepository.save(commentLike);
    }

    // 댓글 좋아요 취소
    public void deleteCommentLike(String commentLikeId) {

        CommentLike commentLike = getCommentLike(commentLikeId);

        // 찾아서 없다면 삭제와 다른게 없다
        if(commentLike == null) {
            return;
        }

        // 삭제 가능한 유저인지
        if(!checkUserInfo(commentLike.getOauth().getOauthId())){
            throw new CustomException(ErrorCode.FORBIDDEN_RESOURCE_ACCESS);
        }

        commentLikeCommandRepository.deleteById(commentLikeId);
    }

    private Comment getComment(String commentId) {

        return commentQueryRepository.findById(commentId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_COMMENT)
        );
    }

    private UserInfo getUserInfo() {

        return userInfoQueryRepository.findById(UserInfoUtil.getOAuthId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );
    }

    private CommentLike getCommentLike(String commentLikeId) {

        return commentLikeCommandRepository.findById(commentLikeId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_COMMENT_LIKE)
        );
    }

    private boolean checkUserInfo(String oAuthId) {

        return oAuthId.equals(UserInfoUtil.getOAuthId());
    }
}
