package com.notfound.lpickbackend.support.query.dto;

import com.notfound.lpickbackend.support.command.domain.Notice;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class NoticeDetailResponse {

    private String noticeId;
    private String title;
    private String content;
    private String author;
    private Instant createdAt;

    public NoticeDetailResponse(Notice notice) {
        this.noticeId = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.author = notice.getAuthor();
        this.createdAt = notice.getCreatedAt();
    }
}
