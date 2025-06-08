package com.notfound.lpickbackend.userinfo.command.application.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "auth")
public class Auth {
    @Id
    @Column(name = "auth_id", nullable = false, length = 40)
    private String authId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

}