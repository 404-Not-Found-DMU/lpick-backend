package com.notfound.lpickbackend.wikipage.command.repository;

import com.notfound.lpickbackend.wikipage.command.application.domain.WikiBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WikiBookmarkRepository extends JpaRepository<WikiBookmark, String> {

    long deleteAllByWiki_WikiId(String wikiId);
}
