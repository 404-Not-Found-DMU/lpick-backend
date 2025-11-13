package com.notfound.lpickbackend.support.query.service;


import com.notfound.lpickbackend.support.query.repository.AnswerQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerQueryService {

    private final AnswerQueryRepository AnswerQueryRepository;

}
