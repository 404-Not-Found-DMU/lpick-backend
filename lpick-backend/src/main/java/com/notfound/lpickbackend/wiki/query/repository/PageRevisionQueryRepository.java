package com.notfound.lpickbackend.wiki.query.repository;

import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PageRevisionQueryRepository extends JpaRepository<PageRevision, String> {

    long countByWiki_WikiId(String wikiId);

    Optional<PageRevision> findByWiki_WikiIdAndRevisionNumber(String wikiId, String revisionNumber);

    Page<PageRevision> findAllByWiki_WikiId(String wikiId, Pageable pageable);

    Optional<PageRevision> findByWiki_WikiId(String wikiId);

    @EntityGraph(attributePaths = {"wiki"})  // 이미 WikiPage를 페치하도록 설정
    @Query(value = """
        SELECT DISTINCT ON (pr.wiki_id)
               pr.*
          FROM page_revision pr
         ORDER BY pr.wiki_id, pr.created_at DESC
        """,
            countQuery = """
        SELECT COUNT(DISTINCT wiki_id)
          FROM page_revision
        """,
            nativeQuery = true)
    Page<PageRevision> findLatestRevisionPerWiki(Pageable pageable);
}
