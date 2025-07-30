package com.notfound.lpickbackend.community.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.community.command.application.dto.CommentCreate;
import com.notfound.lpickbackend.community.command.application.dto.CommentUpdate;
import com.notfound.lpickbackend.community.command.domain.Article;
import com.notfound.lpickbackend.community.command.domain.Comment;
import com.notfound.lpickbackend.community.command.domain.CommentStatus;
import com.notfound.lpickbackend.community.command.repository.CommentCommandRepository;
import com.notfound.lpickbackend.community.query.repository.ArticleQueryRepository;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import com.notfound.lpickbackend.userinfo.query.repository.UserInfoQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentCommandService {

    private final CommentCommandRepository commentCommandRepository;
    private final UserInfoQueryRepository userInfoQueryRepository;
    private final ArticleQueryRepository articleQueryRepository;

    @Transactional
    public void createComment(String articleId, CommentCreate commentCreate) {

        Article article = getArticle(articleId);

        Comment comment = Comment.builder()
                .article(article)
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
        if (checkUserInfo(updatedComment)) {
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

        Comment parent = getComment(commentId);

        if(parent.checkHasParentComment()) { // 대댓글에 대댓글 작성을 막는 예외처리
            throw new CustomException(ErrorCode.ALREADY_HAS_PARENTS_REQUEST);
        }

        Comment comment = Comment.builder()
                .article(parent.getArticle())
                .content(commentCreate.getComment())
                .isDel(CommentStatus.N)
                .oauth(getUserInfo())
                .parentComment(parent)
                .build();

        commentCommandRepository.save(comment);
    }

    // 서비스 내부에서 사용할 UserInfo 찾는 메소드
    private UserInfo getUserInfo() {
        return userInfoQueryRepository.findById(UserInfoUtil.getOAuthId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );
    }

    private Article getArticle(String articleId) {
        return articleQueryRepository.findById(articleId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE)
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
