package com.notfound.lpickbackend.community.query.repository;

import com.notfound.lpickbackend.community.command.domain.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleLikeQueryRepository extends JpaRepository<ArticleLike, String> {

    @Query("""
    SELECT COUNT(al) > 0
    FROM ArticleLike al
    WHERE al.oauth.oauthId = :oauthId AND al.article.articleId = :articleId
    """)
    boolean existsByOauthIdAndArticleId(@Param("oauthId") String oauthId, @Param("articleId") String articleId);
}
