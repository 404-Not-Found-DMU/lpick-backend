package com.notfound.lpickbackend.common.elasticsearch.document;

import com.notfound.lpickbackend.servicedata.command.application.domain.Album;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;

@Getter
@Builder
@Document(indexName = "albums") // Elasticsearch 인덱스 이름 지정
@Mapping(mappingPath = "elasticsearch/album-mapping.json") // 매핑 파일 경로 (선택 사항)
@Setting(settingPath = "elasticsearch/album-settings.json") // 설정 파일 경로 (선택 사항)
@ToString
public class AlbumDocument {

    @Id
    @Field(type = FieldType.Keyword) // 정확한 일치 검색에 유리
    private String albumId;

    @Field(
            type = FieldType.Text,
            analyzer = "autocomplete_analyzer",
            // 검색 시: 일반적인 Nori 분석기 사용 (일반 검색 품질 유지)
            searchAnalyzer = "korean_analyzer"
    )
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard") // 간단한 텍스트 검색
    private String profile;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private Instant releaseDate;

    @Field(type = FieldType.Keyword)
    private String releaseCountry;

    @Field(type = FieldType.Keyword)
    private String label;

    @Field(type = FieldType.Keyword)
    private String lpti;

    // wiki_id는 JPA 엔티티에만 존재하고, Elasticsearch에는 필요하다면 ID만 저장하거나
    // 검색에 필요한 필드만 포함할 수 있습니다. 여기서는 제외하거나 ID만 포함합니다.
    @Field(type = FieldType.Keyword)
    private String wikiId; // WikiPage 엔티티의 ID

    // JPA 엔티티를 Document로 변환하는 헬퍼 메서드
    public static AlbumDocument from(Album album) {
        return AlbumDocument.builder()
                .albumId(album.getAlbumId())
                .name(album.getName())
                .profile(album.getProfile())
                .releaseDate(album.getReleaseDate())
                .releaseCountry(album.getReleaseCountry())
                .label(album.getLabel())
                .lpti(album.getLpti())
                .wikiId(album.getWiki() != null ? album.getWiki().getWikiId() : null)
                .build();
    }
}