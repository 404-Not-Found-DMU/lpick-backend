package com.notfound.lpickbackend.support.query.dto;

import com.notfound.lpickbackend.support.command.domain.Answer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerInfo {

    private String answerId;
    private String title;
    private String content;
    private String author;

    public AnswerInfo(Answer answer) {
        this.answerId = answer.getId();
        this.title = answer.getTitle();
        this.content = answer.getContent();
        this.author = answer.getAuthor();
    }

}
