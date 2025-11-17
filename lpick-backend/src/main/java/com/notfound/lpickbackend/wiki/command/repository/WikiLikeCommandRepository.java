package com.notfound.lpickbackend.wiki.command.repository;

import com.notfound.lpickbackend.wiki.command.application.domain.WikiLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WikiLikeCommandRepository extends JpaRepository<WikiLike, String> {
    Optional<WikiLike> findByWiki_WikiIdAndOauth_OauthId(String wikiId, String oauthId);

    boolean existsByWiki_WikiIdAndOauth_OauthId(String wikiId, String oauthId);
}
