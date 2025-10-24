package com.notfound.lpickbackend.common.elasticsearch.document;

import com.notfound.lpickbackend.servicedata.command.application.domain.Artist;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;

@Getter
@Builder
@Document(indexName = "artists")
@Mapping(mappingPath = "elasticsearch/artist-mapping.json")
@Setting(settingPath = "elasticsearch/document-settings.json")
@ToString
public class ArtistDocument {

    // 통합 검색을 위한 타입 상수 정의
    public static final String DOCUMENT_TYPE = "Artist";

    @Id
    @Field(type = FieldType.Keyword)
    private String artistId;

    @Field(
            type = FieldType.Text,
            analyzer = "autocomplete_analyzer",
            searchAnalyzer = "korean_analyzer"
    )
    private String name;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private Instant debutAt;

    @Field(type = FieldType.Keyword)
    private String groupName;

    @Field(type = FieldType.Keyword)
    private String company;

    @Field(type = FieldType.Keyword)
    private String wikiId; // WikiPage 엔티티의 ID

    public static ArtistDocument from(Artist artist) {
        return ArtistDocument.builder()
                .artistId(artist.getArtistId())
                .name(artist.getName())
                .debutAt(artist.getDebutAt())
                .groupName(artist.getGroupName())
                .company(artist.getCompany())
                .wikiId(artist.getWiki() != null ? artist.getWiki().getWikiId() : null)
                .build();
    }
}
