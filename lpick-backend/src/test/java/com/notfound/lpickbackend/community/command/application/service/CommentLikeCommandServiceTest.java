package com.notfound.lpickbackend.community.command.application.service;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentLikeCommandServiceTest {


    private static final String COMMENT_ID = "comment-123";
    private static final String USER_ID = "user-456";

    @Mock CommentLikeCommandRepository likeRepo;
    @Mock CommentQueryRepository commentRepo;
    @Mock UserInfoQueryRepository userRepo;

    @InjectMocks
    CommentLikeCommandService service;

    private CustomException assertThrowsWithOauth(ErrorCode expected, Runnable invoke) {
        try (MockedStatic<UserInfoUtil> mocked = mockStatic(UserInfoUtil.class)) {
            mocked.when(UserInfoUtil::getOAuthId).thenReturn(USER_ID);
            CustomException ex = assertThrows(CustomException.class, invoke::run);
            assertEquals(expected, ex.getErrorCode());
            return ex;
        }
    }

    @Test
    void createCommentLike_NOT_FOUND_COMMENT() {
        when(commentRepo.findById(COMMENT_ID)).thenReturn(Optional.empty());

        assertThrowsWithOauth(ErrorCode.NOT_FOUND_COMMENT,
                () -> service.createCommentLike(COMMENT_ID));

        verify(commentRepo).findById(COMMENT_ID);
        verifyNoMoreInteractions(commentRepo, userRepo, likeRepo);
    }

    @Test
    void createCommentLike_NOT_FOUND_USER_INFO() {
        Comment comment = mock(Comment.class);
        when(commentRepo.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(userRepo.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrowsWithOauth(ErrorCode.NOT_FOUND_USER_INFO,
                () -> service.createCommentLike(COMMENT_ID));

        verify(commentRepo).findById(COMMENT_ID);
        verify(userRepo).findById(USER_ID);
        verifyNoMoreInteractions(commentRepo, userRepo, likeRepo);
    }

    @Test
    void createCommentLike_ALREADY_HAS_LIKE() {
        Comment comment = mock(Comment.class);
        UserInfo user = mock(UserInfo.class);
        CommentLike like = mock(CommentLike.class);

        when(commentRepo.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(userRepo.findById(USER_ID)).thenReturn(Optional.of(user));
        when(likeRepo.findByOauthAndComment(user, comment)).thenReturn(Optional.of(like));

        assertThrowsWithOauth(ErrorCode.ALREADY_HAS_LIKE,
                () -> service.createCommentLike(COMMENT_ID));

        verify(commentRepo).findById(COMMENT_ID);
        verify(userRepo).findById(USER_ID);
        verify(likeRepo).findByOauthAndComment(user, comment);
        verifyNoMoreInteractions(commentRepo, userRepo, likeRepo);
    }

    @Test
    void deleteCommentLike_NOT_FOUND_COMMENT() {
        when(commentRepo.findById(COMMENT_ID)).thenReturn(Optional.empty());

        assertThrowsWithOauth(ErrorCode.NOT_FOUND_COMMENT,
                () -> service.deleteCommentLike(COMMENT_ID));

        verify(commentRepo).findById(COMMENT_ID);
        verifyNoMoreInteractions(commentRepo, userRepo, likeRepo);
    }

    @Test
    void deleteCommentLike_NOT_FOUND_USER_INFO() {
        Comment comment = mock(Comment.class);
        when(commentRepo.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(userRepo.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrowsWithOauth(ErrorCode.NOT_FOUND_USER_INFO,
                () -> service.deleteCommentLike(COMMENT_ID));

        verify(commentRepo).findById(COMMENT_ID);
        verify(userRepo).findById(USER_ID);
        verifyNoMoreInteractions(commentRepo, userRepo, likeRepo);
    }

    @Test
    void deleteCommentLike_FORBIDDEN_RESOURCE_ACCESS() {
        Comment comment = mock(Comment.class);
        UserInfo user = mock(UserInfo.class);
        CommentLike like = mock(CommentLike.class);
        UserInfo otherUser = mock(UserInfo.class);

        when(commentRepo.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(userRepo.findById(USER_ID)).thenReturn(Optional.of(user));
        when(likeRepo.findByOauthAndComment(user, comment)).thenReturn(Optional.of(like));
        when(otherUser.getOauthId()).thenReturn("other-user-id");
        when(like.getOauth()).thenReturn(otherUser);

        assertThrowsWithOauth(ErrorCode.FORBIDDEN_RESOURCE_ACCESS,
                () -> service.deleteCommentLike(COMMENT_ID));

        verify(commentRepo).findById(COMMENT_ID);
        verify(userRepo).findById(USER_ID);
        verify(likeRepo).findByOauthAndComment(user, comment);
        verify(like).getOauth();
        verifyNoMoreInteractions(commentRepo, userRepo, likeRepo, like);
    }
}