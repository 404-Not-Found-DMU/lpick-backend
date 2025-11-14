package com.notfound.lpickbackend.support.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.support.command.application.dto.NoticeRequest;
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
    public String createNotice(NoticeRequest noticeCreateRequest) {

        Notice notice = new Notice(noticeCreateRequest);

        noticeCommandRepository.save(notice);

        return notice.getId();
    }

    @Transactional
    public void updateNotice(NoticeRequest noticeUpdateRequest, String noticeId) {

        Notice notice = getNoticeById(noticeId);

        notice.updateNotice(noticeUpdateRequest);
    }

    @Transactional
    public void deleteNotice(String noticeId) {
        noticeCommandRepository.deleteById(noticeId);
    }

    private Notice getNoticeById(String noticeId) {
        return noticeCommandRepository.findById(noticeId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_NOTICE)
        );
    }
}
