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
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentLikeCommandService {

    private final CommentLikeCommandRepository commentLikeCommandRepository;
    private final CommentQueryRepository commentQueryRepository;
    private final UserInfoQueryRepository userInfoQueryRepository;

    // 댓글 좋아요 추가
    @Transactional
    public void createCommentLike(String commentId) {

        Comment comment = getComment(commentId);
        UserInfo userInfo = getUserInfo();

        Optional<CommentLike> commentLike = getCommentLike(userInfo, comment);

        // 중복이 아닐 때 저장
        if(commentLike.isEmpty()) {

             CommentLike newCommentLike = CommentLike.builder()
                     .oauth(userInfo)
                     .comment(comment)
                     .build();

             commentLikeCommandRepository.save(newCommentLike);
        } else {
            throw new CustomException(ErrorCode.ALREADY_HAS_LIKE);
        }
    }

    // 댓글 좋아요 취소
    @Transactional
    public void deleteCommentLike(String commentId) {

        Comment comment = getComment(commentId);

        UserInfo userInfo = getUserInfo();

        Optional<CommentLike> commentLike = getCommentLike(userInfo, comment);

        // 찾아서 없다면 삭제와 다른게 없다
        if(commentLike.isEmpty()) {
            return;
        }

        CommentLike deleteCommentLike = commentLike.get();

        // 삭제 가능한 유저인지
        if(!checkUserInfo(deleteCommentLike.getOauth().getOauthId())){
            throw new CustomException(ErrorCode.FORBIDDEN_RESOURCE_ACCESS);
        }

        commentLikeCommandRepository.delete(deleteCommentLike);
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

    private Optional<CommentLike> getCommentLike(UserInfo userInfo, Comment comment) {

        return commentLikeCommandRepository.findByOauthAndComment(userInfo, comment);
    }

    private boolean checkUserInfo(String oAuthId) {

        return oAuthId.equals(UserInfoUtil.getOAuthId());
    }
}
