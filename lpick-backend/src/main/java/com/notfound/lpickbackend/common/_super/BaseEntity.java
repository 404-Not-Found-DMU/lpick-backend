package com.notfound.lpickbackend.common._super;


import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

// SuperBuilder == 자식클래스 빌더 사용 시 슈퍼 클래스 필드까지 빌더에서 활용 가능하게 하는 빌더. 슈퍼 클래스에도 아래와 같이 작성해두어야함.
@SuperBuilder
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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
