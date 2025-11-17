package com.notfound.lpickbackend.userinfo.query.dto.response;


import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.ExpertRequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ExpertAdvancementUserResponse {
    private ExpertRequestStatus status;
    private String requestMemo; // 신청 내역
    private String rejectedCause; // 반려된 경우 사유
    private Instant createdAt; // 신청 시각
    private Instant modifiedAt; // 승인, 반려 시각
}
