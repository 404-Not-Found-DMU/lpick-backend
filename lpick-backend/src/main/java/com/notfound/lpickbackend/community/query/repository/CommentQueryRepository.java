package com.notfound.lpickbackend.community.query.repository;

import com.notfound.lpickbackend.community.command.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentQueryRepository extends JpaRepository<Comment, String>, CustomCommentQueryRepository {


}
