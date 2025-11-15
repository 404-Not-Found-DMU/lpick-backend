package com.notfound.lpickbackend.userinfo.command.application.domain.entity;

import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import com.notfound.lpickbackend.servicedata.command.application.domain.Gear;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_gear")
public class UserGear {

    @PrePersist
    public void prePersist() {
        if (this.userGearId == null) {
            this.userGearId = IdPrefixUtil.get(this.getClass().getSimpleName()) + "_" + UUID.randomUUID();
        }
    }

    @Id
    @Column(name = "user_gear_id", nullable = false, length = 40)
    private String userGearId;

    @Column(name = "is_favorite", nullable = false)
    private boolean isFavorite;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eq_id", nullable = false)
    private Gear eq;

}