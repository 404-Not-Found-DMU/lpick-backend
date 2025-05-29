package com.notfound.lpickbackend.wikipage.command.application.domain;

import com.notfound.lpickbackend.AUTO_ENTITIES.Album;
import com.notfound.lpickbackend.AUTO_ENTITIES.Artist;
import com.notfound.lpickbackend.AUTO_ENTITIES.Debate;
import com.notfound.lpickbackend.AUTO_ENTITIES.Gear;
import com.notfound.lpickbackend.AUTO_ENTITIES.TOOL.IdPrefixUtil;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
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
    private WikiStatus wikiStatus;

    // debate는 debateChat에 비하면 양이 많지 않으므로, debate의 remove 자체는 cascade로 구현해도 성능 손해 심하지 않을 듯하다.
    @OneToMany(mappedBy = "wiki", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Debate> debates = new ArrayList<>();

    @OneToOne(mappedBy = "artistId",
            fetch = FetchType.LAZY)
    private Artist artist;

    @OneToOne(mappedBy = "albumId",
            fetch = FetchType.LAZY)
    private Album album;

    @OneToOne(mappedBy = "eqId",
            fetch = FetchType.LAZY)
    private Gear gear;

    public void updateCurrentRevision(String newRevisionNumber) {
        this.currentRevision = newRevisionNumber;
    }


    public void updateWikiStatus(String wikiStatusStr) {
        for(WikiStatus statusType : WikiStatus.values()) {
            if(statusType.name().equals(wikiStatusStr.toUpperCase())) {
                this.wikiStatus = statusType;
                return;
            }
        }

        // 규칙에 맞지 않는 Enum 기입된 경우 예외처리
        throw new CustomException(ErrorCode.INVALID_FIELD_DATA);
    }
}