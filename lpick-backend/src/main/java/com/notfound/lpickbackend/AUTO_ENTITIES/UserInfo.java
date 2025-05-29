package com.notfound.lpickbackend.AUTO_ENTITIES;

import com.notfound.lpickbackend.security.util.UserInfoCreateDTO;
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

    // dto를 사용한 생성자 (추후 수정?)
    public UserInfo(UserInfoCreateDTO dto) {
        this.oauthId = dto.getOauthId();
        this.oauthType = dto.getOauthType();
        this.nickname = dto.getNickname();
        this.profile = dto.getProfile();
        this.point = dto.getPoint();
        this.stackPoint = dto.getStackPoint();
        this.about = dto.getAbout();
        this.lpti = dto.getLpti();
        this.tier = dto.getTier();
    }
}