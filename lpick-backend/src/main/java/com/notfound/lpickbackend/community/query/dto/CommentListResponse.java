package com.notfound.lpickbackend.community.query.dto;


import java.time.Instant;

public record CommentListResponse(
        boolean isReplyComment, // 해당 comment가 일반 댓글인지 아니면 답글인지 확인
        String parentCommentWriterName, // 본 댓글이 답글인 경우, 답글 대상이었던 댓글의 작성자의 닉네임
        String commentValue,
        Instant createdAt,
        String articleId, // 게시글의 id 확인
        String articleTitle, // 게시글의 타이틀 확인
        String articleWriterName, // 게시글의 작성자 닉네임 확인
        long commentLikeCount // 해당 comment에 대한 like 수 확인
) {
}
