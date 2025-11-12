package com.notfound.lpickbackend.common._event.point;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 포인트 제공 시 발생시킬 이벤트 별 종류와 제공 포인트 모음 */
@Getter
@AllArgsConstructor
public enum ActivityType {

    WRITE_ARTICLE("게시글 작성", 30),
    WRITE_COMMENT("댓글 작성", 10),
    WRITE_PAGE_REVISION("위키 문서 업데이트", 50),
    WRITE_DEBATE("토론 발안", 50),
    WRITE_DEBATE_CHAT("토론 내 의견 제시", 20),

    ACCEPTED_DEBATE_RESULT("발안한 토론 수용됨", 200),
    BALLOT_DEBATE("토론 결정 투표에 참여", 5)
    ;


    private final String type;
    private final int point;
}
