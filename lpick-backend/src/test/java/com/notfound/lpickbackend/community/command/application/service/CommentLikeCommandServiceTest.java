package com.notfound.lpickbackend.community.command.application.service;

import static org.junit.jupiter.api.Assertions.*;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.community.command.domain.Comment;
import com.notfound.lpickbackend.community.command.domain.CommentLike;
import com.notfound.lpickbackend.community.command.repository.CommentLikeCommandRepository;
import com.notfound.lpickbackend.community.query.repository.CommentQueryRepository;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.userinfo.query.repository.UserInfoQueryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentLikeCommandServiceTest {


    private final CommentLikeCommandRepository likeRepo = mock(CommentLikeCommandRepository.class);
    private final CommentQueryRepository commentRepo = mock(CommentQueryRepository.class);
    private final UserInfoQueryRepository userRepo = mock(UserInfoQueryRepository.class);

    private final CommentLikeCommandService service = new CommentLikeCommandService(
            likeRepo, commentRepo, userRepo
    );

    private static final String COMMENT_ID = "comment-123";
    private static final String USER_ID = "user-456";

    @Test
    void createCommentLike_NOT_FOUND_COMMENT() {
        when(commentRepo.findById(COMMENT_ID)).thenReturn(Optional.empty());

        try (MockedStatic<UserInfoUtil> mocked = mockStatic(UserInfoUtil.class)) {
            mocked.when(UserInfoUtil::getOAuthId).thenReturn(USER_ID);

            assertThrows(CustomException.class, () ->
                    service.createCommentLike(COMMENT_ID), ErrorCode.NOT_FOUND_COMMENT.name());
        }
    }

    @Test
    void createCommentLike_NOT_FOUND_USER_INFO() {
        Comment comment = mock(Comment.class);
        when(commentRepo.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(userRepo.findById(USER_ID)).thenReturn(Optional.empty());

        try (MockedStatic<UserInfoUtil> mocked = mockStatic(UserInfoUtil.class)) {
            mocked.when(UserInfoUtil::getOAuthId).thenReturn(USER_ID);

            assertThrows(CustomException.class, () ->
                    service.createCommentLike(COMMENT_ID), ErrorCode.NOT_FOUND_USER_INFO.name());
        }
    }

    @Test
    void createCommentLike_ALREADY_HAS_LIKE() {
        Comment comment = mock(Comment.class);
        UserInfo user = mock(UserInfo.class);
        CommentLike like = mock(CommentLike.class);

        when(commentRepo.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(userRepo.findById(USER_ID)).thenReturn(Optional.of(user));
        when(likeRepo.findByOauthAndComment(user, comment)).thenReturn(Optional.of(like));

        try (MockedStatic<UserInfoUtil> mocked = mockStatic(UserInfoUtil.class)) {
            mocked.when(UserInfoUtil::getOAuthId).thenReturn(USER_ID);

            assertThrows(CustomException.class, () ->
                    service.createCommentLike(COMMENT_ID), ErrorCode.ALREADY_HAS_LIKE.name());
        }
    }

    @Test
    void deleteCommentLike_NOT_FOUND_COMMENT() {
        when(commentRepo.findById(COMMENT_ID)).thenReturn(Optional.empty());

        try (MockedStatic<UserInfoUtil> mocked = mockStatic(UserInfoUtil.class)) {
            mocked.when(UserInfoUtil::getOAuthId).thenReturn(USER_ID);

            assertThrows(CustomException.class, () ->
                    service.deleteCommentLike(COMMENT_ID), ErrorCode.NOT_FOUND_COMMENT.name());
        }
    }

    @Test
    void deleteCommentLike_NOT_FOUND_USER_INFO() {
        Comment comment = mock(Comment.class);
        when(commentRepo.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(userRepo.findById(USER_ID)).thenReturn(Optional.empty());

        try (MockedStatic<UserInfoUtil> mocked = mockStatic(UserInfoUtil.class)) {
            mocked.when(UserInfoUtil::getOAuthId).thenReturn(USER_ID);

            assertThrows(CustomException.class, () ->
                    service.deleteCommentLike(COMMENT_ID), ErrorCode.NOT_FOUND_USER_INFO.name());
        }
    }

    @Test
    void deleteCommentLike_FORBIDDEN_RESOURCE_ACCESS() {
        Comment comment = mock(Comment.class);
        UserInfo user = mock(UserInfo.class);
        CommentLike like = mock(CommentLike.class);

        when(commentRepo.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(userRepo.findById(USER_ID)).thenReturn(Optional.of(user));
        when(likeRepo.findByOauthAndComment(user, comment)).thenReturn(Optional.of(like));

        // 좋아요 객체가 다른 유저의 것이라고 가정
        UserInfo otherUser = mock(UserInfo.class);
        when(otherUser.getOauthId()).thenReturn("other-user-id");
        when(like.getOauth()).thenReturn(otherUser);

        try (MockedStatic<UserInfoUtil> mocked = mockStatic(UserInfoUtil.class)) {
            mocked.when(UserInfoUtil::getOAuthId).thenReturn(USER_ID);

            assertThrows(CustomException.class, () ->
                    service.deleteCommentLike(COMMENT_ID), ErrorCode.FORBIDDEN_RESOURCE_ACCESS.name());
        }
    }
}