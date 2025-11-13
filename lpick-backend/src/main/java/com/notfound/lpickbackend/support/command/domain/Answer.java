package com.notfound.lpickbackend.support.command.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @Column(name = "oauth_id", nullable = false, length = 40)
    private String oauthId;

    @Size(max = 100)
    @Column(name = "title", length = 100)
    private String title;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "question_id", nullable = false)
    private Answer question;

    @NotNull
    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    private String content;

}