package com.notfound.lpickbackend.support.command.domain;

import com.notfound.lpickbackend.support.command.application.dto.NoticeCreateRequest;
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
@Table(name = "notice")
public class Notice {
    @Id
    @Size(max = 40)
    @Column(name = "id", nullable = false, length = 40)
    private String id;

    @Size(max = 40)
    @NotNull
    @Column(name = "author", nullable = false, length = 40)
    private String author;

    @Size(max = 100)
    @NotNull
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Size(max = 10)
    @NotNull
    @Column(name = "content", nullable = false, length = 10)
    private String content;


    public Notice(NoticeCreateRequest noticeCreateRequest) {
        this.author = noticeCreateRequest.getAuthor();
        this.title = noticeCreateRequest.getTitle();
        this.content = noticeCreateRequest.getContent();
    }
}