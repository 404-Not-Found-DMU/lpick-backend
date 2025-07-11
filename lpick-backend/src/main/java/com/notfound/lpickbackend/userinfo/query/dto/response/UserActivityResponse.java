package com.notfound.lpickbackend.userinfo.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserActivityResponse {
    public UserActivityResponse(int articleCount, int commentCount, int wikiEditCount) {
        this.articleCount = articleCount;
        this.commentCount = commentCount;
        this.wikiEditCount = wikiEditCount;
    }

    private int articleCount;
    private int commentCount;
    private int wikiEditCount; // 순수하게 위키 편집에만 기준하는가? 아니면 위키 토론 참여 등을 포함하는가? => 우선 위키 편집(== PageRevision 수정 아이디) 기준함.
    // private int likeCount; // 위키 내 리뷰, 아티스트, 댓글, 게시글 모두 좋아요의 대상이 될 수 있는데, 무엇을 기준하는가? => 우선 게시글에 기준함.
    // likeCount는 '폴리모픽 테이블 구현' 또는 'Postgresql 선언적 파티셔닝'에 기반해 DB를 재정의하기 전까지 진행 XXX -> 반환하지 않기.
}
