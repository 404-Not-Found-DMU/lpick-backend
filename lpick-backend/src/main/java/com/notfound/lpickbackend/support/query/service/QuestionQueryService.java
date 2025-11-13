package com.notfound.lpickbackend.support.query.service;


import com.notfound.lpickbackend.support.query.repository.QuestionQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionQueryService {

    private final QuestionQueryRepository QuestionQueryRepository;

}
