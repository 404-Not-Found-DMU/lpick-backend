package com.notfound.lpickbackend.wiki.query.repository;

import com.notfound.lpickbackend.wiki.command.application.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WikiReviewQueryRepository extends JpaRepository<Review, String> {

    boolean existsByWiki_wikiIdAndOauth_oauthId(String wikiId, String oauthId);

    Optional<Review> findByWiki_wikiIdANdOauth_oauthId(String wikiId, String oauthId);

    Page<Review> findAllByWiki_wikiId(String wikiId, Pageable pageable);
}
