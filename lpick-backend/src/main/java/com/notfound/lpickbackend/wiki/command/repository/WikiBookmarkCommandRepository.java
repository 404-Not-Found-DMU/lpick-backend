package com.notfound.lpickbackend.wiki.command.repository;

import com.notfound.lpickbackend.wiki.command.application.domain.WikiBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WikiBookmarkCommandRepository extends JpaRepository<WikiBookmark, String> {

    long deleteAllByWiki_WikiId(String wikiId);

    void deleteByWiki_wikiIdAndOauth_oauthId(String wikiId, String oauthId);
}
