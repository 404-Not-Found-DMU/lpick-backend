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
@Table(name = "comment_like")
public class CommentLike {
    @Id
    @Column(name = "comment_like_id", nullable = false, length = 40)
    private String commentLikeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

}