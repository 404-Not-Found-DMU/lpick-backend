package com.notfound.lpickbackend.wiki.command.repository;

import com.notfound.lpickbackend.wiki.command.application.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WikiReviewCommandRepository extends JpaRepository<Review, String> {
    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END
        FROM Review r
        WHERE r.reviewId = :reviewId
          AND r.oauth.oauthId = :userId
    """)
    boolean isEqualUser(@Param("userId") String userId, @Param("reviewId") String reviewId);
}
