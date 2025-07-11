package com.notfound.lpickbackend.community.query.service;

import com.notfound.lpickbackend.community.query.repository.CommentQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentQueryService {
    private final CommentQueryRepository commentQueryRepository;

    public int countCommentByOauthId(String oauthId) {
        return commentQueryRepository.countByUserInfo_OauthId(oauthId);
    }
}
