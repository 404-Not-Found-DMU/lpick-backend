package com.notfound.lpickbackend.userinfo.command.application.domain.entity;

import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import com.notfound.lpickbackend.servicedata.command.application.domain.inherenceENUM.MusicGenre;
import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.ExpertRequestStatus;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.ExpertAdvancementRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.userdetails.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "expert_request")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ExpertRequest {

    @Id
    @Column(name = "expert_request_id", length = 40)
    private String expertRequestId;

    @PrePersist
    public void prePersist() {
        if (this.expertRequestId == null) {
            this.expertRequestId = IdPrefixUtil.get(this.getClass().getSimpleName()) + "_" + UUID.randomUUID();
        }
    }

    /**
     * 1(UserInfo) : N(ExpertRequest)
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo userInfo;

    /**
     * 생성 시각 - 신청이 처음 들어온 시점
     * Spring Data JPA Auditing으로 자동 세팅.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * 승낙/반려시 시각 기록
     */

    @Column(name = "decision_at")
    private Instant decisionAt;

    /**
     * null  : 아직 심사 안 함(대기)
     * true  : 승인
     * false : 반려
     */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ExpertRequestStatus status;

    /**
     * S3에 저장된 파일 키(또는 파일명)의 리스트.
     * 예: ["expert-requests/uuid_file1.pdf", "expert-requests/uuid_file2.png"]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_file_json", columnDefinition = "jsonb", nullable = false)
    @Builder.Default
    private List<String> requestFileJson = new ArrayList<>();

    /**
     * 사용자가 전문분야로설정해둔 장르 목록
     * 예: ["expert-requests/uuid_file1.pdf", "expert-requests/uuid_file2.png"]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "music_genre", columnDefinition = "jsonb", nullable = false)
    @Builder.Default
    private List<MusicGenre> musicGenre = new ArrayList<>();

    /**
     * 신청 시 사용자 메모
     */
    @Column(name = "request_memo", length = 1000)
    private String requestMemo;

    /**
     * 반려 사유 메모.
     */
    @Column(name = "rejected_cause", length = 1000)
    private String rejectedCause;

    @Column(name = "name", nullable = false, length = 50)
    protected String name;
    @Column(name = "email", nullable = false, length = 40)
    protected String email;

    @Column(name = "ph_num", nullable = false, length = 14)
    protected String phNum;
    @Column(name = "affiliation", nullable = false, length = 40)
    protected String affiliation; // 소속


    public void markApproved() {
        this.decisionAt = Instant.now();
        this.status = ExpertRequestStatus.APPROVED;
    }

    public void markRejected(String rejectedCause) {
        this.decisionAt = Instant.now();
        this.status = ExpertRequestStatus.REJECTED;
        this.rejectedCause = rejectedCause;
    }
}