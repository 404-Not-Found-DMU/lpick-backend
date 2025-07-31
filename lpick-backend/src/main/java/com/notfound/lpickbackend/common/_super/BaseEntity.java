package com.notfound.lpickbackend.common._super;


import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseEntity {
    @Id
    @Column(name = "id", nullable = false, length = 40)
    private String id;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = IdPrefixUtil.get(this.getClass().getSimpleName()) + "_" + UUID.randomUUID();
        }
    }
}
