package com.notfound.lpickbackend.userinfo.command.application.domain.embed;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class NotificationSetting {

    private Boolean isAlarmWikiEdit; // 북마크한 위키 수정되면 알림

    private Boolean isAlarmNewDebateAnswer; // 참여한 토론 내 새 글 추가 시 알림

    private Boolean isAlarmCommented; // 내 게시글에 댓글 달리면 알림

    private Boolean isAlarmEvent; // 서비스 내 이벤트 시 알림

}
