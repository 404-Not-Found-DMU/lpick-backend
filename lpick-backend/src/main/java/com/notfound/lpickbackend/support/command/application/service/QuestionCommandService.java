package com.notfound.lpickbackend.support.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import com.notfound.lpickbackend.support.command.application.dto.QuestionRequest;
import com.notfound.lpickbackend.support.command.domain.Question;
import com.notfound.lpickbackend.support.command.repository.QuestionCommandRepository;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.userinfo.command.repository.UserInfoCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionCommandService {

    private final QuestionCommandRepository questionCommandRepository;
    private final UserInfoCommandRepository userInfoCommandRepository;

    @Transactional
    public String createQuestion(QuestionRequest questionRequest) {

        String oauthId = UserInfoUtil.getOAuthId();

        Question question = new Question(questionRequest, getUserInfo(oauthId));

        questionCommandRepository.save(question);

        return question.getId();
    }

    @Transactional
    public void updateQuestion(QuestionRequest questionRequest, String questionId) {

        String oauthId = UserInfoUtil.getOAuthId();

        Question question = getQuestion(questionId);

        if(!oauthId.equals(question.getOauth().getOauthId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_RESOURCE_ACCESS);
        }

        question.updateQuestion(questionRequest);
    }

    @Transactional
    public void deleteQuestion(String questionId) {

        String oauthId = UserInfoUtil.getOAuthId();

        Question question = getQuestion(questionId);

        if(!oauthId.equals(question.getOauth().getOauthId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_RESOURCE_ACCESS);
        }

        questionCommandRepository.delete(question);
    }

    private Question getQuestion(String questionId) {
        return questionCommandRepository.findById(questionId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_QUESTION)
        );
    }

    private UserInfo getUserInfo(String oAuthId) {
        return userInfoCommandRepository.findByOauthId(oAuthId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );
    }
}
