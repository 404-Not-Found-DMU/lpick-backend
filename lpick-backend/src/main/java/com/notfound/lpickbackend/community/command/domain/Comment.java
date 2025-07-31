package com.notfound.lpickbackend.community.command.domain;

import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "comment")
@SQLDelete(sql = "UPDATE comment SET is_del = 'Y' WHERE comment_id = ?")
public class Comment {
    @Id
    @Column(name = "comment_id", nullable = false, length = 40)
    private String commentId;

    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    private String content;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "modified_at")
    private Instant modifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_del", nullable = false, length = 10)
    private CommentStatus isDel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;

    @OneToMany(mappedBy = "parentComment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Where(clause = "is_del = 'N'") // 삭제된 댓글 제외
    private List<Comment> childComments = new ArrayList<>();

    // 게시글의 삭제 여부 체크 메소드
    public boolean checkIsDel() {
        return isDel.equals(CommentStatus.Y);
    }

    // 부모 댓글 여부 체크 메소드
    public boolean checkHasParentComment() {
        return parentComment != null;
    }

    @PrePersist
    public void prePersist() {
        if (this.commentId == null) {
            this.commentId = IdPrefixUtil.get(this.getClass().getSimpleName()) + "_" + UUID.randomUUID();
        }

        this.createdAt = Instant.now();
        this.modifiedAt = Instant.now();
    }
}