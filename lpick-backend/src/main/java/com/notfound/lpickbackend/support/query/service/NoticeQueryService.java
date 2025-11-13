package com.notfound.lpickbackend.support.query.service;


import com.notfound.lpickbackend.support.query.repository.NoticeQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeQueryService {

    private final NoticeQueryRepository noticeQueryRepository;

}
