package com.notfound.lpickbackend.support.command.domain;

import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import com.notfound.lpickbackend.support.command.application.dto.AnswerRequest;
import com.notfound.lpickbackend.support.command.application.dto.NoticeRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "answer")
public class Answer {
    @Id
    @Size(max = 40)
    @Column(name = "id", nullable = false, length = 40)
    private String id;

    @Size(max = 40)
    @NotNull
    @Column(name = "author", nullable = false, length = 40)
    private String author;

    @Size(max = 100)
    @Column(name = "title", length = 100)
    private String title;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Lob
    @NotNull
    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    private String content;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "modified_at")
    private Instant modifiedAt;

    public Answer(AnswerRequest answerRequest, Question question) {
        this.author = answerRequest.getAuthor();
        this.title = answerRequest.getTitle();
        this.content = answerRequest.getContent();
        this.question = question;
    }

    public void updateAnswer(AnswerRequest answerRequest) {
        this.title = answerRequest.getTitle();
        this.content = answerRequest.getContent();
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