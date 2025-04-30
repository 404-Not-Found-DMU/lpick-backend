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
@Table(name = "article_bookmark")
public class ArticleBookmark {
    @Id
    @Column(name = "article_bookmark_id", nullable = false, length = 40)
    private String articleBookmarkId;

    @Column(name = "oauth_id", nullable = false, length = 40)
    private String oauthId;

    @Column(name = "article_id", nullable = false, length = 40)
    private String articleId;

}