package com.notfound.lpickbackend.Wiki.Command.Repository;

import com.notfound.lpickbackend.AUTO_ENTITIES.PageRevision;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevisionRepository extends JpaRepository<PageRevision, String> {

    /*
    @Query(value = "SELECT * FROM page_revision WHERE page_revision.wiki_id = :wikiId", nativeQuery = true)
    List<PageRevision> findPageRevisionsByWikiId(String wikiId);
    */

    long countByWikiId(String wikiId);
}
