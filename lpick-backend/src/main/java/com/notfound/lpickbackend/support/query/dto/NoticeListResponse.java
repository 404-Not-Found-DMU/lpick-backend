package com.notfound.lpickbackend.support.query.dto;

import com.notfound.lpickbackend.support.command.domain.Notice;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class NoticeListResponse {

    private Long no;
    private String noticeId;
    private String title;
    private String author;
    private Instant createdAt;

    public NoticeListResponse(Notice notice) {
        this.noticeId = notice.getId();
        this.title = notice.getTitle();
        this.author = notice.getAuthor();
        this.createdAt = notice.getCreatedAt();
    }

    public static NoticeListResponse from(Notice notice) {
        return new NoticeListResponse(notice);
    }
}
