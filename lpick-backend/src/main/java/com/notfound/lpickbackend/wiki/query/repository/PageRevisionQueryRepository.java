package com.notfound.lpickbackend.wiki.query.repository;

import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PageRevisionQueryRepository extends JpaRepository<PageRevision, String> {

    long countByWiki_WikiId(String wikiId);

    Optional<PageRevision> findByWiki_WikiIdAndRevisionNumber(String wikiId, String revisionNumber);

    Page<PageRevision> findAllByWiki_WikiId(String wikiId, Pageable pageable);

    Optional<PageRevision> findByWiki_WikiId(String wikiId);

    Optional<PageRevision> findByrevisionNumberAndWiki_wikiId(String revisionNumber, String wikiId);

    // 1단계: Wiki ID만 최신 순으로 페이징해서 가져오기
    @Query("""
      SELECT pr.wiki.wikiId
        FROM PageRevision pr
       WHERE pr.wiki.wikiStatus = 'OPEN'
       GROUP BY pr.wiki.wikiId
       ORDER BY MAX(pr.createdAt) DESC
    """)
    Page<String> findWikiIdsOrderByLatestRevision(Pageable pageable);

    // 2단계: 위키 ID 목록에 대해, 각 위키별 최신 PageRevision을 페치 조인으로 한 번에 조회
    @EntityGraph(attributePaths = {"wiki"})
    @Query(
        value = """
            SELECT pr
            FROM PageRevision pr
            WHERE pr.wiki.wikiId IN :wikiIds
            AND pr.createdAt = (
            SELECT MAX(pr2.createdAt)
            FROM PageRevision pr2
                WHERE pr2.wiki.wikiId = pr.wiki.wikiId
            )
            ORDER BY pr.createdAt DESC
        """,
        countQuery = """
            SELECT COUNT(DISTINCT pr.wiki.wikiId)
            FROM PageRevision pr
            WHERE pr.wiki.wikiId IN :wikiIds
        """) // 페이징을 위해 countQuery 추가
    Page<PageRevision> findLatestRevisionsForWikis(@Param("wikiIds") List<String> wikiIds, Pageable pageable);
}
