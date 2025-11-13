package com.notfound.lpickbackend.support.command.application.service;

import com.notfound.lpickbackend.support.command.application.dto.NoticeCreateRequest;
import com.notfound.lpickbackend.support.command.domain.Notice;
import com.notfound.lpickbackend.support.command.repository.NoticeCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeCommandService {

    private final NoticeCommandRepository noticeCommandRepository;

    @Transactional
    public void createNotice(NoticeCreateRequest noticeCreateRequest) {

        Notice notice = new Notice(noticeCreateRequest);

        noticeCommandRepository.save(notice);
    }
}
