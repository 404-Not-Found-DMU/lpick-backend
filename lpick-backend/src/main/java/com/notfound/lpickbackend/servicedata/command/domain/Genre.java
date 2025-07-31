package com.notfound.lpickbackend.servicedata.command.domain;

import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "genre")
public class Genre {

    @PrePersist
    public void prePersist() {
        if (this.genreId == null) {
            this.genreId = IdPrefixUtil.get(this.getClass().getSimpleName()) + "_" + UUID.randomUUID();
        }
    }
    @Id
    @Column(name = "genre_id", nullable = false, length = 40)
    private String genreId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

}