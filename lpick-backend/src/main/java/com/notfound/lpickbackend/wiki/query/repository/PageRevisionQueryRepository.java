package com.notfound.lpickbackend.wiki.query.repository;

import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PageRevisionQueryRepository extends JpaRepository<PageRevision, String> {

    long countByWiki_WikiId(String wikiId);

    Optional<PageRevision> findByWiki_WikiIdAndRevisionNumber(String wikiId, String revisionNumber);

    Page<PageRevision> findAllByWikiId(String wikiId, Pageable pageable);

    long deleteByWikiId(String wikiId);
}
