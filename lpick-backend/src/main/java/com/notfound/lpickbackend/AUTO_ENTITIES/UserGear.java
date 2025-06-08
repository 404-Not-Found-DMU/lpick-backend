package com.notfound.lpickbackend.AUTO_ENTITIES;

import com.notfound.lpickbackend.userinfo.command.application.domain.UserInfo;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_gear")
public class UserGear {
    @Id
    @Column(name = "user_gear_id", nullable = false, length = 40)
    private String userGearId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eq_id", nullable = false)
    private Gear eq;

}