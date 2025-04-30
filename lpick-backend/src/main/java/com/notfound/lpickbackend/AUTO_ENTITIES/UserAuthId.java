package com.notfound.lpickbackend.AUTO_ENTITIES;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class UserAuthId implements Serializable {
    private static final long serialVersionUID = 1534237739702382332L;
    @Column(name = "auth_id", nullable = false, length = 50)
    private String authId;

    @Column(name = "oauth_id", nullable = false, length = 40)
    private String oauthId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserAuthId entity = (UserAuthId) o;
        return Objects.equals(this.oauthId, entity.oauthId) &&
                Objects.equals(this.authId, entity.authId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oauthId, authId);
    }

}