package com.notfound.lpickbackend.community.query.application.service;

import com.notfound.lpickbackend.community.query.application.dto.ParentsCommentResponse;
import com.notfound.lpickbackend.community.query.repository.CommentQueryRepository;
import com.notfound.lpickbackend.security.util.UserInfoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentQueryService {

    private final CommentQueryRepository commentQueryRepository;

    public Page<ParentsCommentResponse> readCommentList(
            String articleId,
            Pageable pageable
    ) {
        String oauthId = UserInfoUtil.getOAuthId();

        return commentQueryRepository.findParentsCommentsWithChildrenAndLikes(articleId, oauthId, pageable);
    }
}
