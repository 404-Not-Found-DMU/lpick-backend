package com.notfound.lpickbackend.community.query.application.service;

import com.notfound.lpickbackend.community.query.repository.CommentQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentQueryService {

    private final CommentQueryRepository commentQueryRepository;
}
