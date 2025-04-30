package com.notfound.lpickbackend.AUTO_ENTITIES;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "review_like")
public class ReviewLike {
    @Id
    @Column(name = "review_like_id", nullable = false, length = 40)
    private String reviewLikeId;

    @Column(name = "oauth_id", nullable = false, length = 40)
    private String oauthId;

    @Column(name = "review_id", nullable = false, length = 40)
    private String reviewId;

}