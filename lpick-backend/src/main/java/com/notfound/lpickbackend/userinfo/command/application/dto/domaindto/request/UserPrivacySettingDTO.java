package com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrivacySettingDTO {
    // embedded 클래스는 JPA와 연관된 기능들이 추가되므로, DTO로 활용하기엔 무거움. -> 해당 DTO 별도 구현한 사유입니다. UserNotificationSettingDTO도 동일.
    // DTO는 Boolean 래퍼 클래스, 실제 Embedded는 boolean 원시형으로 구현하였음.
    // 래퍼 클래스로 설정 시, valid 기반 null 값 체크 가능(원시형은 기본값 false므로 체크 불가)
    // RequestBody에서 1차 확인, 엔티티에서 notnull로 2차확인

    @NotNull(message = "활동 횟수 표기 여부는 true, false 중 하나여야 합니다.")
    private Boolean allowViewActCount; // 게시글, 댓글, 위키 편집 등의 횟수 표기 여부

    @NotNull(message = "최근 활동 표기 여부는 true, false 중 하나여야 합니다.")
    private Boolean allowViewRecentAct; // 최근 활동 내역 표기 여부

    @NotNull(message = "개인 장비 표기 여부는 true, false 중 하나여야 합니다.")
    private Boolean allowViewGear; // 개인 장비 표기 여부

    @NotNull(message = "앨범 목록 표기 여부는 true, false 중 하나여야 합니다.")
    private Boolean allowViewCollection; // 앨범 목록 표기 여부
}
