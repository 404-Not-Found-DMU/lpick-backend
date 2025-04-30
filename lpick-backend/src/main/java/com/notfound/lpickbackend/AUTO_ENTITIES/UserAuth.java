package com.notfound.lpickbackend.AUTO_ENTITIES;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_auth")
public class UserAuth {
    @EmbeddedId
    private UserAuthId id;

    @MapsId("authId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auth_id", nullable = false)
    private Auth auth;

    @MapsId("oauthId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oauth_id", nullable = false)
    private UserInfo oauth;

}