package com.notfound.lpickbackend.community.query.repository;

import com.notfound.lpickbackend.community.command.domain.Article;
import com.notfound.lpickbackend.community.query.application.dto.ArticleDetailResponseDTO;
import com.notfound.lpickbackend.community.query.application.dto.ArticleListResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleQueryRepository extends JpaRepository<Article, String> {

    // 게시글 id, 제목, 좋아요 개수, 코멘트 개수, 작성자
    /*
    * JPQL 사용 근거(?) : 단순 연관관계 매핑으로 불러올 경우 객체지향적으로는 올바르지만,
    * 조회 된 모든 게시글에 대하여 함께 불러온 좋아요 List, 북마크 List의 Count를 각각 진행해주어야함.
    * 번거롭고 성능 저하 이슈 발생 가능성이 있어 JPQL 사용
    * */
    @Query("""
    SELECT new com.notfound.lpickbackend.community.query.application.dto.ArticleListResponseDTO(
        a.articleId,
        a.title,
        COUNT(DISTINCT l),
        COUNT(DISTINCT c),
        COUNT(DISTINCT b),
        a.oauth.oauthId
    )
    FROM Article a
    LEFT JOIN ArticleLike l ON l.article = a
    LEFT JOIN Comment c ON c.article = a
    LEFT JOIN ArticleBookmark b ON b.article = a
    WHERE a.isDel = com.notfound.lpickbackend.community.command.domain.ArticleStatus.N
    GROUP BY a.articleId, a.title, a.oauth
    """)
    Page<ArticleListResponseDTO> findAllWithLikeAndCommentAndBookmarkCount(Pageable pageable);

    @Query("""
    SELECT new com.notfound.lpickbackend.community.query.application.dto.ArticleListResponseDTO(
        a.articleId,
        a.title,
        COUNT(DISTINCT l),
        COUNT(DISTINCT c),
        COUNT(DISTINCT b),
        a.oauth.oauthId
    )
    FROM Article a
    LEFT JOIN ArticleLike l ON l.article = a
    LEFT JOIN Comment c ON c.article = a
    LEFT JOIN ArticleBookmark b ON b.article = a
    WHERE a.isDel = com.notfound.lpickbackend.community.command.domain.ArticleStatus.N
    AND a.oauth.oauthId = :oauthId
    GROUP BY a.articleId, a.title, a.oauth
    """)
    Page<ArticleListResponseDTO> findMyWithLikeAndCommentAndBookmarkCount(@Param("oauthId") String oauthId, Pageable pageable);

    @Query("""
    SELECT new com.notfound.lpickbackend.community.query.application.dto.ArticleListResponseDTO(
        a.articleId,
        a.title,
        COUNT(DISTINCT l2),
        COUNT(DISTINCT c),
        COUNT(DISTINCT b),
        a.oauth.oauthId
    )
    FROM ArticleLike l
    JOIN l.article a
    LEFT JOIN ArticleLike l2 ON l2.article = a
    LEFT JOIN Comment c ON c.article = a
    LEFT JOIN ArticleBookmark b ON b.article = a
    WHERE l.oauth.oauthId = :oauthId
    AND a.isDel = com.notfound.lpickbackend.community.command.domain.ArticleStatus.N
    GROUP BY a.articleId, a.title, a.oauth
    """)
    Page<ArticleListResponseDTO> findMyLikedWithLikeAndCommentAndBookmarkCount(@Param("oauthId") String oauthId, Pageable pageable);

    // content를 포함한 게시글 상세조회
    @Query("""
    SELECT new com.notfound.lpickbackend.community.query.application.dto.ArticleDetailResponseDTO(
        a.articleId,
        a.title,
        a.content,
        COUNT(DISTINCT l),
        COUNT(DISTINCT c),
        COUNT(DISTINCT b),
        a.oauth.oauthId
    )
    FROM Article a
    LEFT JOIN ArticleLike l ON l.article = a
    LEFT JOIN Comment c ON c.article = a
    LEFT JOIN ArticleBookmark b ON b.article = a
    WHERE a.isDel = com.notfound.lpickbackend.community.command.domain.ArticleStatus.N
    AND a.articleId = :articleId
    GROUP BY a.articleId, a.title, a.oauth
    """)
    ArticleDetailResponseDTO findByIdWithLikeAndCommentAndBookmarkCount(@Param("articleId") String articleId);
}
