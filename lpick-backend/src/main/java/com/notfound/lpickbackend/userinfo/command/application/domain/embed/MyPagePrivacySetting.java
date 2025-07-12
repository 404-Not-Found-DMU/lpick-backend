package com.notfound.lpickbackend.userinfo.command.application.domain.embed;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 사용자 환경설정 - UserSetting의 필드 목적 Embeddable 클래스.*/
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MyPagePrivacySetting {
    private Boolean allowViewActCount; // 게시글, 댓글, 위키 편집 등의 횟수 표기 여부
    private Boolean allowViewRecentAct; // 최근 활동 내역 표기 여부
    private Boolean allowViewGear; // 개인 장비 표기 여부
    private Boolean allowViewCollection; // 앨범 목록 표기 여부

}
