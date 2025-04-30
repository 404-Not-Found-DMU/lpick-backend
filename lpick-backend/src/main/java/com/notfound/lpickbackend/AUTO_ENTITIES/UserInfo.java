package com.notfound.lpickbackend.AUTO_ENTITIES;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_info")
public class UserInfo {
    @Id
    @Column(name = "oauth_id", nullable = false, length = 40)
    private String oauthId;

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

    @Column(name = "tier_id", nullable = false, length = 40)
    private String tierId;

}