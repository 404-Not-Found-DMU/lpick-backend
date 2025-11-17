package com.notfound.lpickbackend.servicedata.query.inherenceEnum;

public enum CommentListFilter {
    ALL,           // 전체
    ONLY_COMMENT,  // 부모 댓글만 (parentComment IS NULL)
    ONLY_REPLY,    // 대댓글만 (parentComment IS NOT NULL)
    LIKE_DESC      // 좋아요 많은 순
}
