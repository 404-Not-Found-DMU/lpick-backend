package com.notfound.lpickbackend.support.query.dto;

import com.notfound.lpickbackend.support.command.domain.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAndAnswerDetailResponse {

    private String questionId;
    private String title;
    private String author;
    private String content;
    private Instant createdAt;
    private Instant modifiedAt;
    private boolean isAnswered;

    private AnswerInfo answerInfo;

    public static QuestionAndAnswerDetailResponse from(Question question) {
        return new QuestionAndAnswerDetailResponse(question);
    }

    public QuestionAndAnswerDetailResponse(Question question) {

        this.questionId = question.getId();
        this.title = question.getTitle();
        this.content = question.getContent();
        this.author = question.getOauth().getNickname();
        this.createdAt = question.getCreatedAt();
        this.modifiedAt = question.getModifiedAt();
        this.isAnswered = question.isAnswered();

        if(isAnswered) {
            this.answerInfo = new AnswerInfo(question.getAnswer());
        } else {
            this.answerInfo = null;
        }
    }

}
