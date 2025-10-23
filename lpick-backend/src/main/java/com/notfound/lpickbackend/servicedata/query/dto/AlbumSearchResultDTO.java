package com.notfound.lpickbackend.servicedata.query.dto;

import com.notfound.lpickbackend.common.elasticsearch.document.AlbumDocument;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;


@Getter
@Builder
public class AlbumSearchResultDTO {

    private String albumId;
    private String name;
    private String profile;
    private Instant releaseDate;
    private String releaseCountry;
    private String label;
    private String lpti;

    // Document를 DTO로 변환하는 정적 팩토리 메서드
    public static AlbumSearchResultDTO from(AlbumDocument document) {
        return AlbumSearchResultDTO.builder()
                .albumId(document.getAlbumId())
                .name(document.getName())
                .profile(document.getProfile())
                .releaseDate(document.getReleaseDate())
                .releaseCountry(document.getReleaseCountry())
                .label(document.getLabel())
                .lpti(document.getLpti())
                .build();
    }
}
