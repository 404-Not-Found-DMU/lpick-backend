package com.notfound.lpickbackend.community.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.community.command.application.dto.CommentCreate;
import com.notfound.lpickbackend.community.command.application.dto.CommentUpdate;
import com.notfound.lpickbackend.community.command.domain.Comment;
import com.notfound.lpickbackend.community.command.domain.CommentStatus;
import com.notfound.lpickbackend.community.command.repository.CommentCommandRepository;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import com.notfound.lpickbackend.userinfo.query.repository.UserInfoQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentCommandService {

    private final CommentCommandRepository commentCommandRepository;
    private final UserInfoQueryRepository userInfoQueryRepository;

    @Transactional
    public void createComment(CommentCreate commentCreate) {

        Comment comment = Comment.builder()
                .content(commentCreate.getComment())
                .isDel(CommentStatus.N)
                .oauth(getUserInfo())
                .build();

        commentCommandRepository.save(comment);
    }

    @Transactional
    public void updateComment(String commentId, CommentUpdate commentUpdate) {

        Comment updatedComment = getComment(commentId);
        String userId = UserInfoUtil.getOAuthId();

        // 삭제여부 확인
        if(updatedComment.checkIsDel()) {
            throw new CustomException(ErrorCode.NOT_FOUND_COMMENT);
        }

        // 접근 가능 여부 확인
        if (userId.equals(updatedComment.getOauth().getOauthId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_RESOURCE_ACCESS);
        }

        commentUpdate.setComment(updatedComment.getContent());
        commentCommandRepository.save(updatedComment);
    }

    @Transactional
    public void deleteComment(String commentId) {

        Comment deleteComment = getComment(commentId);
        String userId = UserInfoUtil.getOAuthId();

        if (checkUserInfo(deleteComment)) {
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
        }

        commentCommandRepository.delete(deleteComment);
    }

    @Transactional
    public void createChildComment(String commentId, CommentCreate commentCreate) {

        Comment comment = Comment.builder()
                .content(commentCreate.getComment())
                .isDel(CommentStatus.N)
                .oauth(getUserInfo())
                .parentComment(getComment(commentId))
                .build();

        commentCommandRepository.save(comment);
    }

    // 서비스 내부에서 사용할 UserInfo 찾는 메소드
    private UserInfo getUserInfo() {
        return userInfoQueryRepository.findById(UserInfoUtil.getOAuthId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );
    }

    private boolean checkUserInfo(Comment comment) {
        return !comment.getOauth().getOauthId().equals(UserInfoUtil.getOAuthId());
    }

    private Comment getComment(String commentId) {
        return commentCommandRepository.findById(commentId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_COMMENT)
        );
    }
}
