package com.notfound.lpickbackend.community.query.repository;

import com.notfound.lpickbackend.community.command.domain.Comment;
import com.notfound.lpickbackend.community.query.dto.CommentListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentQueryRepository extends JpaRepository<Comment, String>, CustomCommentQueryRepository {

    int countByOauth_OauthId(String oauthId);
}
