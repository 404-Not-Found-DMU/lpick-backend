package com.notfound.lpickbackend.common._event.point;

import java.time.Instant;

/** 이벤트 발행 시 핸들러에 전달할 필요 최소내역 DTO */
public record PointAccrualRequestedEvent(
        String userId,
        ActivityType activity,
        String sourceId, // 게시글, 댓글 등 사용자가 새로 만든/수정한 대상의 pk
        Instant issuedAt
) {
}
