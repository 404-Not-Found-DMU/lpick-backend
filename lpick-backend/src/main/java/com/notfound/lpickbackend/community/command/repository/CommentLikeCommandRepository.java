package com.notfound.lpickbackend.community.command.repository;

import com.notfound.lpickbackend.community.command.domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeCommandRepository extends JpaRepository<CommentLike, String> {
}
