package com.notfound.lpickbackend.community.query.service;

import com.notfound.lpickbackend.community.query.dto.ArticleBookmarkListResponse;
import com.notfound.lpickbackend.community.query.repository.ArticleBookmarkQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleBookmarkQueryService {
    private final ArticleBookmarkQueryRepository articleBookmarkQueryRepository;


    public Page<ArticleBookmarkListResponse> getArticleBookmarkListByOauthId(String oauthId, Pageable pageable) {
        return articleBookmarkQueryRepository.findBookmarksByUser(oauthId, pageable);
    }
}
