package com.notfound.lpickbackend.support.command.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

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

    @Size(max = 40)
    @NotNull
    @Column(name = "oauth_id", nullable = false, length = 40)
    private String oauthId;

    @Size(max = 100)
    @NotNull
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @NotNull
    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    private String content;

}