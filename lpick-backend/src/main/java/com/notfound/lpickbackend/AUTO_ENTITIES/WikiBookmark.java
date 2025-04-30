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
@Table(name = "wiki_bookmark")
public class WikiBookmark {
    @Id
    @Column(name = "wiki_bookmark_id", nullable = false, length = 40)
    private String wikiBookmarkId;

    @Column(name = "oauth_id", nullable = false, length = 40)
    private String oauthId;

    @Column(name = "wiki_id", nullable = false, length = 40)
    private String wikiId;

}