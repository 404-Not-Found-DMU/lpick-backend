package com.notfound.lpickbackend.community.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.community.command.application.dto.CommentUpdate;
import com.notfound.lpickbackend.community.command.domain.Comment;
import com.notfound.lpickbackend.community.command.repository.CommentCommandRepository;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
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
class CommentCommandServiceTest {

    private static final String COMMENT_ID = "test-comment-id";
    private static final String CURRENT_USER_ID = "user-123";

    @InjectMocks private CommentCommandService sut;
    @Mock private CommentCommandRepository commentCommandRepository;

    @Test
    void updateComment_deleted_throws_NOT_FOUND_COMMENT() {
        // given
        Comment deleted = mock(Comment.class);
        when(deleted.checkIsDel()).thenReturn(true);
        when(commentCommandRepository.findById(COMMENT_ID)).thenReturn(Optional.of(deleted));

        try (MockedStatic<UserInfoUtil> mocked = mockStatic(UserInfoUtil.class)) {
            mocked.when(UserInfoUtil::getOAuthId).thenReturn(CURRENT_USER_ID);

            // when
            CustomException ex = assertThrows(CustomException.class,
                    () -> sut.updateComment(COMMENT_ID, new CommentUpdate()));

            // then
            assertEquals(ErrorCode.NOT_FOUND_COMMENT, ex.getErrorCode());
        }
    }

    @Test
    void updateComment_other_user_throws_FORBIDDEN() {
        Comment comment = mock(Comment.class);
        when(comment.checkIsDel()).thenReturn(false);
        when(comment.getOauth()).thenReturn(UserInfo.builder().oauthId("other-user").build());
        when(commentCommandRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        try (MockedStatic<UserInfoUtil> mocked = mockStatic(UserInfoUtil.class)) {
            mocked.when(UserInfoUtil::getOAuthId).thenReturn(CURRENT_USER_ID);

            CustomException ex = assertThrows(CustomException.class,
                    () -> sut.updateComment(COMMENT_ID, new CommentUpdate()));

            assertEquals(ErrorCode.FORBIDDEN_RESOURCE_ACCESS, ex.getErrorCode());
        }
    }

    @Test
    void deleteComment_other_user_throws_AUTHENTICATION_FAILED() {
        Comment comment = mock(Comment.class);
        when(comment.getOauth()).thenReturn(UserInfo.builder().oauthId("other-user").build());
        when(commentCommandRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        try (MockedStatic<UserInfoUtil> mocked = mockStatic(UserInfoUtil.class)) {
            mocked.when(UserInfoUtil::getOAuthId).thenReturn(CURRENT_USER_ID);

            CustomException ex = assertThrows(CustomException.class,
                    () -> sut.deleteComment(COMMENT_ID));

            assertEquals(ErrorCode.AUTHENTICATION_FAILED, ex.getErrorCode());
        }
    }

}