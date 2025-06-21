package com.notfound.lpickbackend.community.command.application.domain;

import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.Instant;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@SQLDelete(sql = "UPDATE article SET is_del = 'Y' WHERE article_id = ?") // repository.delete 호출시 사용됨.
@Table(name = "article")
public class Article {
    @Id
    @Column(name = "article_id", nullable = false, length = 40)
    private String articleId;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    private String content;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "modified_at")
    private Instant modifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_del", nullable = false, length = 10)
    private ArticleStatus isDel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;

    @PrePersist
    public void prePersist() {
        if (this.articleId == null) {
            this.articleId = IdPrefixUtil.get(this.getClass().getSimpleName()) + "_" + UUID.randomUUID();
        }

        this.createdAt = Instant.now();
        this.modifiedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedAt = Instant.now();
    }

    public void updateContent(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 게시글의 삭제 여부 체크 메소드
    public boolean checkIsDel() {
        return isDel.equals(ArticleStatus.Y);
    }
}