package com.notfound.lpickbackend.support.query.service;


import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.common.util.Specs;
import com.notfound.lpickbackend.support.command.domain.Question;
import com.notfound.lpickbackend.support.query.dto.QuestionAndAnswerDetailResponse;
import com.notfound.lpickbackend.support.query.dto.QuestionAndAnswerListResponse;
import com.notfound.lpickbackend.support.query.repository.QuestionQueryRepository;
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
public class QuestionQueryService {

    private final QuestionQueryRepository QuestionQueryRepository;
    private final QuestionQueryRepository questionQueryRepository;

    @Transactional(readOnly = true)
    public Page<QuestionAndAnswerListResponse> searchQuestion(String keyword, Pageable pageable) {

        Specification<Question> specs = Specs.all(
                QuestionSpec.keywordLike(keyword)
        );

        Page<QuestionAndAnswerListResponse> page = questionQueryRepository.findAll(specs, pageable).map(QuestionAndAnswerListResponse::from);

        // 넘버링에 사용할 변수
        long total = page.getTotalElements();
        long start = total - pageable.getOffset();

        // AtomicLong : long값을 안전하게 증가/증감 하는 객체.. 저도 처음봤습니다.
        AtomicLong counter = new AtomicLong(start);
        page.forEach(p -> p.setNo(counter.getAndDecrement()));

        return page;
    }

    public QuestionAndAnswerDetailResponse readQuestionDetail(String questionId) {

        Question question = questionQueryRepository.findById(questionId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_QUESTION)
        );

        return new QuestionAndAnswerDetailResponse(question);
    }
}
