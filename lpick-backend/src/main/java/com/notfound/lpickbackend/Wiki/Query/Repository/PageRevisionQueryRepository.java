package com.notfound.lpickbackend.Wiki.Query.Repository;

import com.notfound.lpickbackend.AUTO_ENTITIES.PageRevision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PageRevisionQueryRepository extends JpaRepository<PageRevision, String> {

    long countByWiki_WikiId(String wikiId);

    Optional<PageRevision> findByWiki_WikiIdAndRevisionNumber(String wikiId, String revisionNumber);
}
