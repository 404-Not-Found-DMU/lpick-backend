package com.notfound.lpickbackend.community.command.application.service;

import static org.junit.jupiter.api.Assertions.*;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.community.command.application.dto.CommentUpdate;
import com.notfound.lpickbackend.community.command.domain.Comment;
import com.notfound.lpickbackend.community.command.repository.CommentCommandRepository;
import com.notfound.lpickbackend.community.query.repository.ArticleQueryRepository;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.userinfo.query.repository.UserInfoQueryRepository;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentCommandServiceTest {

    @InjectMocks
    private CommentCommandService commentCommandService;

    @Mock
    private CommentCommandRepository commentCommandRepository;

    @Mock
    private UserInfoQueryRepository userInfoQueryRepository;

    @Mock
    private ArticleQueryRepository articleQueryRepository;

    @Mock
    private UserInfoUtil userInfoUtil;

    private final String COMMENT_ID = "test-comment-id";
    private final String CURRENT_USER_ID = "user-123";

    private UserInfo currentUser;

    @BeforeEach
    void setUp() {
        currentUser = UserInfo.builder()
                .oauthId(CURRENT_USER_ID)
                .build();
    }

    @Test
    void updateComment_deleted_test() {
        // given
        Comment deletedComment = mock(Comment.class);
        when(deletedComment.checkIsDel()).thenReturn(true);

        when(commentCommandRepository.findById(COMMENT_ID)).thenReturn(Optional.of(deletedComment));

        // when & then
        assertThrows(CustomException.class, () ->
                        commentCommandService.updateComment(COMMENT_ID, new CommentUpdate()),
                ErrorCode.NOT_FOUND_COMMENT.name());
    }

    @Test
    void updateComment_bad_request_test() {
        // given
        Comment comment = mock(Comment.class);
        UserInfo otherUser = UserInfo.builder().oauthId("other-user").build();

        when(comment.getOauth()).thenReturn(otherUser);
        when(comment.checkIsDel()).thenReturn(false);
        when(commentCommandRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        // 현재 로그인한 유저 아이디 설정
        mockStatic(UserInfoUtil.class).when(UserInfoUtil::getOAuthId).thenReturn(CURRENT_USER_ID);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () ->
                commentCommandService.updateComment(COMMENT_ID, new CommentUpdate()));

        assert exception.getErrorCode() == ErrorCode.FORBIDDEN_RESOURCE_ACCESS;
    }

    @Test
    void deleteComment_another_user_test() {
        // given
        Comment comment = mock(Comment.class);
        UserInfo otherUser = UserInfo.builder().oauthId("other-user").build();

        when(comment.getOauth()).thenReturn(otherUser);
        when(commentCommandRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        mockStatic(UserInfoUtil.class).when(UserInfoUtil::getOAuthId).thenReturn(CURRENT_USER_ID);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () ->
                commentCommandService.deleteComment(COMMENT_ID));

        assert exception.getErrorCode() == ErrorCode.AUTHENTICATION_FAILED;
    }

}