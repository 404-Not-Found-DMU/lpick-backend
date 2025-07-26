package com.notfound.lpickbackend.community.query.repository;

import com.notfound.lpickbackend.community.command.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentQueryRepository extends JpaRepository<Comment, String> {
}
