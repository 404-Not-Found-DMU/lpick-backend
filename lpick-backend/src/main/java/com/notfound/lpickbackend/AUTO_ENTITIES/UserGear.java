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
@Table(name = "user_gear")
public class UserGear {
    @Id
    @Column(name = "user_gear_id", nullable = false, length = 40)
    private String userGearId;

    @Column(name = "oauth_id", nullable = false, length = 40)
    private String oauthId;

    @Column(name = "eq_id", nullable = false, length = 40)
    private String eqId;

}