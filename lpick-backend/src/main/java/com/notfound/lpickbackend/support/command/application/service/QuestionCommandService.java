package com.notfound.lpickbackend.support.command.application.service;

import com.notfound.lpickbackend.support.command.application.dto.NoticeCreateRequest;
import com.notfound.lpickbackend.support.command.domain.Notice;
import com.notfound.lpickbackend.support.command.repository.QuestionCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionCommandService {

    private final QuestionCommandRepository questionCommandRepository;


}
