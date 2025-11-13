package com.notfound.lpickbackend.support.command.application.service;

import com.notfound.lpickbackend.support.command.repository.AnswerCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerCommandService {

    private final AnswerCommandRepository answerCommandRepository;
}
