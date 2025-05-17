package com.notfound.lpickbackend.AUTO_ENTITIES;

import com.notfound.lpickbackend.wikipage.command.application.domain.WikiPage;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "wiki_bookmark")
public class WikiBookmark {
    @Id
    @Column(name = "wiki_bookmark_id", nullable = false, length = 40)
    private String wikiBookmarkId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wiki_id", nullable = false)
    private WikiPage wiki;

}