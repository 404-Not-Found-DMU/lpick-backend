package com.notfound.lpickbackend.support.command.domain;

import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import com.notfound.lpickbackend.support.command.application.dto.QuestionRequest;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "question")
public class Question {
    @Id
    @Size(max = 40)
    @Column(name = "id", nullable = false, length = 40)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;

    @Size(max = 100)
    @NotNull
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "is_answered", nullable = false)
    private boolean isAnswered;

    @Lob
    @NotNull
    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    private String content;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "modified_at")
    private Instant modifiedAt;

    @OneToOne(mappedBy = "question", fetch = FetchType.EAGER)
    private Answer answer;

    public Question(QuestionRequest questionRequest, UserInfo oauth) {
        this.content = questionRequest.getContent();
        this.title = questionRequest.getTitle();
        this.oauth = oauth;
    }

    public void updateQuestion(QuestionRequest questionRequest) {
        this.content = questionRequest.getContent();
        this.title = questionRequest.getTitle();
        this.modifiedAt = Instant.now();
    }

    public void updateIsAnswered(boolean isAnswered) {
        this.isAnswered = isAnswered;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = IdPrefixUtil.get(this.getClass().getSimpleName()) + "_" + UUID.randomUUID();
        }

        this.createdAt = Instant.now();
        this.modifiedAt = Instant.now();
    }
}