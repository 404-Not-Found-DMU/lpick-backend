package com.notfound.lpickbackend.wiki.command.repository;

import com.notfound.lpickbackend.wiki.command.application.domain.WikiBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WikiBookmarkCommandRepository extends JpaRepository<WikiBookmark, String> {

    long deleteAllByWiki_WikiId(String wikiId);

    void deleteByWiki_wikiIdAndOauth_oauthId(String wikiId, String oauthId);

    Optional<WikiBookmark> findByWiki_WikiIdAndOauth_OauthId(String wikiId, String oauthId);
}
