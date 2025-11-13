package com.notfound.lpickbackend.support.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class QuestionCreateRequest {

    private String title;
    private String content;
}
