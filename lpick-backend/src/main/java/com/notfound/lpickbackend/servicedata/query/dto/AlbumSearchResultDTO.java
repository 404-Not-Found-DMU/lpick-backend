package com.notfound.lpickbackend.servicedata.query.dto;

import com.notfound.lpickbackend.common.elasticsearch.document.AlbumDocument;
import com.notfound.lpickbackend.servicedata.command.application.domain.Album;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
    @Setter
    private String imageUrl;

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
    public static AlbumSearchResultDTO from(Album album) {
        return AlbumSearchResultDTO.builder()
                .albumId(album.getAlbumId())
                .name(album.getName())
                .profile(album.getProfile())
                .releaseDate(album.getReleaseDate())
                .releaseCountry(album.getReleaseCountry())
                .label(album.getLabel())
                .lpti(album.getLpti())
                .build();
    }

}
