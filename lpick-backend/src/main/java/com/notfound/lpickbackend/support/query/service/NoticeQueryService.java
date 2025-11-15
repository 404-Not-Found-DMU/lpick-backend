package com.notfound.lpickbackend.support.query.service;


import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.common.util.Specs;
import com.notfound.lpickbackend.support.command.domain.Notice;
import com.notfound.lpickbackend.support.query.dto.NoticeDetailResponse;
import com.notfound.lpickbackend.support.query.dto.NoticeListResponse;
import com.notfound.lpickbackend.support.query.dto.QuestionAndAnswerListResponse;
import com.notfound.lpickbackend.support.query.repository.NoticeQueryRepository;
import com.notfound.lpickbackend.support.query.util.NoticeSpec;
import com.notfound.lpickbackend.support.query.util.QuestionSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class NoticeQueryService {

    private final NoticeQueryRepository noticeQueryRepository;

    @Transactional(readOnly = true)
    public Page<NoticeListResponse> searchNotice(String keyword, Pageable pageable) {


        Specification<Notice> specs = Specs.all(
                NoticeSpec.keywordLike(keyword)
        );

        Page<NoticeListResponse> page = noticeQueryRepository.findAll(specs, pageable).map(NoticeListResponse::from);

        // 넘버링에 사용할 변수
        long total = page.getTotalElements();
        long start = total - pageable.getOffset();

        // AtomicLong : long값을 안전하게 증가/증감 하는 객체.. 저도 처음봤습니다.
        AtomicLong counter = new AtomicLong(start);
        page.forEach(p -> p.setNo(counter.getAndDecrement()));

        return page;
    }

    @Transactional(readOnly = true)
    public NoticeDetailResponse readNoticeDetail(String noticeId) {

        Notice notice = noticeQueryRepository.findById(noticeId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_NOTICE)
        );

        return new NoticeDetailResponse(notice);
    }
}
