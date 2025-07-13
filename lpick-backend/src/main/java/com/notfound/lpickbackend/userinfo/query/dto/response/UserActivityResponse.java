package com.notfound.lpickbackend.userinfo.query.dto.response;

import com.notfound.lpickbackend.common._super.BlindableResponse;
import lombok.Getter;
import lombok.experimental.SuperBuilder;


@Getter
@SuperBuilder // 자식과 부모가 모두 이 어노테이션 지니면, 자식 클래스에서도 부모 클래스의 필드를 빌더로 활용가능.
        // 단, 상속 깊이가 낮고 noargscon, allargscon 사용 않는 클래스에 활용하는 것 권장한다함. -> 엔티티에는 활용 안하는게 나을듯 함.
public class UserActivityResponse extends BlindableResponse {
    private Integer articleCount;
    private Integer commentCount;
    private Integer wikiEditCount; // 순수하게 위키 편집에만 기준하는가? 아니면 위키 토론 참여 등을 포함하는가? => 우선 위키 편집(== PageRevision 수정 아이디) 기준함.
    // private int likeCount; // 위키 내 리뷰, 아티스트, 댓글, 게시글 모두 좋아요의 대상이 될 수 있는데, 무엇을 기준하는가? => 우선 게시글에 기준함.
    // likeCount는 '폴리모픽 테이블 구현' 또는 'Postgresql 선언적 파티셔닝'에 기반해 DB를 재정의하기 전까지 진행 XXX -> 반환하지 않기.

}