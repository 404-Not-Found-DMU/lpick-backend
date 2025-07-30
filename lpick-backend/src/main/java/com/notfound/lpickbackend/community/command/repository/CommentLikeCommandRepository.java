package com.notfound.lpickbackend.community.command.repository;

import com.notfound.lpickbackend.community.command.domain.Comment;
import com.notfound.lpickbackend.community.command.domain.CommentLike;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeCommandRepository extends JpaRepository<CommentLike, String> {
    Optional<CommentLike> findByOauthAndComment(UserInfo userInfo, Comment comment);
}
