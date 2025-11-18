package com.notfound.lpickbackend.community.query.repository;

import com.notfound.lpickbackend.community.command.domain.ArticleBookmark;
import com.notfound.lpickbackend.community.query.dto.ArticleBookmarkListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = """
        select new com.notfound.lpickbackend.community.query.dto.ArticleBookmarkListResponse(
            a.oauth.nickname,
            cast(a.createdAt as string),
            a.title,
            a.content,
            a.articleId,
            count(distinct al.articleLikeId),
            a.viewCount
        )
        from ArticleBookmark b
            join b.article a
            left join ArticleLike al on al.article = a
        where b.oauth.oauthId = :oauthId
        group by a.articleId, a.oauth.nickname, a.createdAt, a.title, a.content, a.viewCount
        order by a.createdAt desc
    """,
            countQuery = """
        select count(b)
        from ArticleBookmark b
        where b.oauth.oauthId = :oauthId
    """
    )
    Page<ArticleBookmarkListResponse> findBookmarksByUser(
            @Param("oauthId") String oauthId,
            Pageable pageable
    );

}
