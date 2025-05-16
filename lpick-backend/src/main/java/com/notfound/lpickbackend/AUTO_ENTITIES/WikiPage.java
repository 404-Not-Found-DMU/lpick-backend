package com.notfound.lpickbackend.AUTO_ENTITIES;

import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import com.notfound.lpickbackend.wikipage.command.application.domain.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "wiki_page")
public class WikiPage {
    @Id
    @Column(name = "wiki_id", nullable = false, length = 40)
    private String wikiId;

    @PrePersist
    public void prePersist() {
        if (this.wikiId == null) {
            this.wikiId = IdPrefixUtil.get(this.getClass().getSimpleName()) + "_" + UUID.randomUUID();
        }
    }

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "current_revision", length = 50)
    private String currentRevision;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private Status status;

}