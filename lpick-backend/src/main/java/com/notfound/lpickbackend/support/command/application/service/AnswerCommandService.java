package com.notfound.lpickbackend.support.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.support.command.application.dto.AnswerRequest;
import com.notfound.lpickbackend.support.command.domain.Answer;
import com.notfound.lpickbackend.support.command.domain.Question;
import com.notfound.lpickbackend.support.command.repository.AnswerCommandRepository;
import com.notfound.lpickbackend.support.command.repository.QuestionCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnswerCommandService {

    private final AnswerCommandRepository answerCommandRepository;
    private final QuestionCommandRepository questionCommandRepository;

    @Transactional
    public void createAnswer(AnswerRequest answerRequest, String questionId) {

        Question question = getQuestion(questionId);

        Answer answer = new Answer(answerRequest, question);

        question.updateIsAnswered(true);

        answerCommandRepository.save(answer);
    }

    @Transactional
    public void updateAnswer(AnswerRequest answerRequest, String answerId) {

        Answer answer = getAnswer(answerId);

        answer.updateAnswer(answerRequest);
    }

    @Transactional
    public void deleteAnswer(String answerId) {

        String oauthId = UserInfoUtil.getOAuthId();

        Answer answer = getAnswer(answerId);

        Question question = answer.getQuestion();

        question.updateIsAnswered(false);

        answerCommandRepository.delete(answer);
    }

    private Answer getAnswer(String answerId) {
        return answerCommandRepository.findById(answerId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ANSWER)
        );
    }

    private Question getQuestion(String questionId) {
        return questionCommandRepository.findById(questionId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_QUESTION)
        );
    }
}
