package com.notfound.lpickbackend.userinfo.command.application.domain.entity;

import com.notfound.lpickbackend.userinfo.command.application.domain.embed.MyPagePrivacySetting;
import com.notfound.lpickbackend.userinfo.command.application.domain.embed.NotificationSetting;
import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.PageThemeSetting;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.UserNotificationSettingDTO;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.UserPrivacySettingDTO;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.UserSettingEditRequest;
import com.notfound.lpickbackend.userinfo.query.dto.response.UserSettingResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_setting")
public class UserSetting {

    @Id
    private String oauthId; // 엔티티에서 기본키 값 저장 목적으로 사용할 실제 필드

    @MapsId
    @OneToOne
    @JoinColumn(name = "oauth_id")
    private UserInfo oauth; // 위 기본키 값의 원본이 되는 내역과 매핑됨을 증명하는 일대일 매핑 필드

    // 이런식으로 AttributeOverride 설정하지 않으면, Embedded 클래스 명칭까지 필드에 포함되어 테이블 필드 명칭이 지나치게 길어짐.
    // 미설정 시 my_page_privacy_setting_allow_view_act_count 을 -> allow_view_act_count로 설정되도록 변경
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="allowViewActCount",
                    column=@Column(name="allow_view_act_count")),
            @AttributeOverride(name="allowViewRecentAct",
                    column=@Column(name="allow_view_recent_act")),
            @AttributeOverride(name="allowViewGear",
                    column=@Column(name="allow_view_gear")),
            @AttributeOverride(name="allowViewCollection",
                    column=@Column(name="allow_view_collection"))
    })
    private MyPagePrivacySetting myPagePrivacySetting;

    @Enumerated(EnumType.STRING)
    private PageThemeSetting pageThemeSetting;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="isAlarmWikiEdit",
                    column=@Column(name="is_alarm_wiki_edit")),
            @AttributeOverride(name="isAlarmNewDebateAnswer",
                    column=@Column(name="is_alarm_new_debate_answer")),
            @AttributeOverride(name="isAlarmCommented",
            column=@Column(name="is_alarm_commented")),
            @AttributeOverride(name="isAlarmEvent",
            column=@Column(name="is_alarm_event"))
    })
    private NotificationSetting notificationSetting;

    public void setToDefault(UserInfo userInfo) {
        this.oauth = userInfo;

        this.myPagePrivacySetting = MyPagePrivacySetting.builder()
                .allowViewActCount(true)
                .allowViewCollection(true)
                .allowViewRecentAct(true)
                .allowViewGear(true)
                .build();

        this.pageThemeSetting = PageThemeSetting.LIGHT;

        this.notificationSetting = NotificationSetting.builder()
                .isAlarmEvent(true)
                .isAlarmWikiEdit(true)
                .isAlarmNewDebateAnswer(true)
                .isAlarmCommented(true)
                .build();
    }

    public void updateSettingByRequest(UserSettingEditRequest request) {
        this.myPagePrivacySetting = MyPagePrivacySetting.builder()
                .allowViewActCount(request.getPrivacy().getAllowViewActCount())
                .allowViewCollection(request.getPrivacy().getAllowViewCollection())
                .allowViewRecentAct(request.getPrivacy().getAllowViewRecentAct())
                .allowViewGear(request.getPrivacy().getAllowViewGear())
                .build();

        this.pageThemeSetting = request.getTheme();

        this.notificationSetting = NotificationSetting.builder()
                .isAlarmEvent(request.getNotification().getIsAlarmEvent())
                .isAlarmWikiEdit(request.getNotification().getIsAlarmWikiEdit())
                .isAlarmNewDebateAnswer(request.getNotification().getIsAlarmNewDebateAnswer())
                .isAlarmCommented(request.getNotification().getIsAlarmCommented())
                .build();
    }

    public UserSettingResponse toDTO() {
        return UserSettingResponse.builder()
                .privacy(UserPrivacySettingDTO.builder()
                        .allowViewActCount(this.myPagePrivacySetting.getAllowViewActCount())
                        .allowViewCollection(this.myPagePrivacySetting.getAllowViewCollection())
                        .allowViewGear(this.myPagePrivacySetting.getAllowViewGear())
                        .allowViewRecentAct(this.myPagePrivacySetting.getAllowViewRecentAct())
                        .build())
                .theme(this.pageThemeSetting)
                .notification(UserNotificationSettingDTO.builder()
                        .isAlarmCommented(this.notificationSetting.getIsAlarmCommented())
                        .isAlarmEvent(this.notificationSetting.getIsAlarmEvent())
                        .isAlarmNewDebateAnswer(this.notificationSetting.getIsAlarmNewDebateAnswer())
                        .isAlarmWikiEdit(this.notificationSetting.getIsAlarmWikiEdit())
                        .build())
                .build();
    }

}
