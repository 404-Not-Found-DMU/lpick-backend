package com.notfound.lpickbackend.community.query.repository;

import com.notfound.lpickbackend.community.command.domain.ArticleBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleBookmarkQueryRepository extends JpaRepository<ArticleBookmark, String> {

    @Query("""
    SELECT COUNT(ab) > 0
    FROM ArticleBookmark ab
    WHERE ab.oauth.oauthId = :oauthId AND ab.article.articleId = :articleId
    """)
    boolean existsByOauthIdAndArticleId(@Param("oauthId") String oauthId, @Param("articleId") String articleId);
}
