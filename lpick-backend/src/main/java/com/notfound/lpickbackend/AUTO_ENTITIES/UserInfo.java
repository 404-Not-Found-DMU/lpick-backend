package com.notfound.lpickbackend.AUTO_ENTITIES;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_info")
public class UserInfo {
    @Id
    @Column(name = "oauth_id", nullable = false, length = 40)
    private String oauthId;

    @Column(name = "oauth_type", nullable = false, length = 10)
    private String oauthType;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "profile", length = 200)
    private String profile;

    @Column(name = "point", nullable = false)
    private Integer point;

    @Column(name = "stack_point", nullable = false)
    private Integer stackPoint;

    @Column(name = "about", length = Integer.MAX_VALUE)
    private String about;

    @Column(name = "lpti", length = 4)
    private String lpti;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tier_id", nullable = false)
    private Tier tier;

}