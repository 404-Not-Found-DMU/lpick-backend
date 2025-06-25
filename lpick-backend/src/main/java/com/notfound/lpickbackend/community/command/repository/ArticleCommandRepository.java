package com.notfound.lpickbackend.community.command.repository;

import com.notfound.lpickbackend.community.command.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleCommandRepository extends JpaRepository<Article, String> {
}
