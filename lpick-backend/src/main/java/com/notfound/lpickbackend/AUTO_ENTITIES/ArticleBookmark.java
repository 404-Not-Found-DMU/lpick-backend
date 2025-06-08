package com.notfound.lpickbackend.AUTO_ENTITIES;

import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "article_bookmark")
public class ArticleBookmark {
    @Id
    @Column(name = "article_bookmark_id", nullable = false, length = 40)
    private String articleBookmarkId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

}