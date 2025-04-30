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
@Table(name = "comment_like")
public class CommentLike {
    @Id
    @Column(name = "comment_like_id", nullable = false, length = 40)
    private String commentLikeId;

    @Column(name = "oauth_id", nullable = false, length = 40)
    private String oauthId;

    @Column(name = "comment_id", nullable = false, length = 40)
    private String commentId;

}