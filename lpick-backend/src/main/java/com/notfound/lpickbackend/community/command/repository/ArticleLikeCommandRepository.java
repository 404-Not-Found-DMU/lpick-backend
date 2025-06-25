package com.notfound.lpickbackend.community.command.repository;

import com.notfound.lpickbackend.community.command.domain.Article;
import com.notfound.lpickbackend.community.command.domain.ArticleLike;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleLikeCommandRepository extends JpaRepository<ArticleLike, String> {

    Optional<ArticleLike> findByOauthAndArticle(UserInfo userInfo, Article article);
}
