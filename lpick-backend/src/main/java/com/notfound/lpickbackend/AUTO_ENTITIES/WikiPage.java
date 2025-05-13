package com.notfound.lpickbackend.AUTO_ENTITIES;

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
@Table(name = "wiki_page")
public class WikiPage {
    @Id
    @Column(name = "wiki_id", nullable = false, length = 40)
    private String wikiId;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "current_revision", length = 50)
    private String currentRevision;

    @Column(name = "status", nullable = false, length = 10)
    private String status;

}