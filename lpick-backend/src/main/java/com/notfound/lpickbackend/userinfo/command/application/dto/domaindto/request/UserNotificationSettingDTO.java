package com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** UserSetting이 지니는 Embedded 클래스, NotificationSetting의 Valid 검증용 DTO*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotificationSettingDTO {
    // embedded 클래스는 JPA와 연관된 기능들이 추가되므로, DTO로 활용하기엔 무거움. -> 해당 DTO 별도 구현한 사유입니다. UserPrivacySettingDTO도 동일.
    // DTO는 Boolean 래퍼 클래스, 실제 Embedded는 boolean 원시형으로 구현하였음.
    // 래퍼 클래스로 설정 시, valid 기반 null 값 체크 가능(원시형은 기본값 false므로 체크 불가)
    // RequestBody에서 1차 확인, 엔티티에서 notnull로 2차확인

    @NotNull(message = "위키 수정 알림 여부는 true, false 중 하나여야 합니다..")
    private Boolean isAlarmWikiEdit; // 북마크한 위키 수정되면 알림

    @NotNull(message = "토론 새글 알림 여부는 true, false 중 하나여야 합니다.")
    private Boolean isAlarmNewDebateAnswer; // 참여한 토론 내 새 글 추가 시 알림

    @NotNull(message = "신규 댓글 알림 여부는 true, false 중 하나여야 합니다.")
    private Boolean isAlarmCommented; // 내 게시글에 댓글 달리면 알림

    @NotNull(message = "이벤트 알림 여부는 true, false 중 하나여야 합니다.")
    private Boolean isAlarmEvent; // 서비스 내 이벤트 시 알림

}
