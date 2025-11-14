package com.notfound.lpickbackend.support.command.domain;

import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import com.notfound.lpickbackend.support.command.application.dto.NoticeRequest;
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

    @Lob
    @NotNull
    @Column(name = "content", nullable = false, length = 10)
    private String content;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "modified_at")
    private Instant modifiedAt;

    public Notice(NoticeRequest noticeCreateRequest) {
        this.author = noticeCreateRequest.getAuthor();
        this.title = noticeCreateRequest.getTitle();
        this.content = noticeCreateRequest.getContent();
    }

    public void updateNotice(NoticeRequest noticeUpdateRequest) {
        this.author = noticeUpdateRequest.getAuthor();
        this.title = noticeUpdateRequest.getTitle();
        this.content = noticeUpdateRequest.getContent();
        this.modifiedAt = Instant.now();
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