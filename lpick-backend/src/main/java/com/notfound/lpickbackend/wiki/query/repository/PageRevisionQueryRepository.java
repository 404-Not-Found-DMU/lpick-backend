package com.notfound.lpickbackend.wiki.query.repository;

import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PageRevisionQueryRepository extends JpaRepository<PageRevision, String> {

    long countByWiki_WikiId(String wikiId);

    Optional<PageRevision> findByWiki_WikiIdAndRevisionNumber(String wikiId, String revisionNumber);
}
