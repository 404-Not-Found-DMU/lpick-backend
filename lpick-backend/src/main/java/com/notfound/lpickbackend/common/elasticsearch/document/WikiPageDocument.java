package com.notfound.lpickbackend.common.elasticsearch.document;

import com.notfound.lpickbackend.servicedata.command.application.domain.Album;
import com.notfound.lpickbackend.servicedata.command.application.domain.Artist;
import com.notfound.lpickbackend.servicedata.command.application.domain.Gear;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@Builder
@Document(indexName = "wikipages") // Elasticsearch 인덱스 이름 지정
@Mapping(mappingPath = "elasticsearch/wikipage-mapping.json") // 매핑 파일 경로 (선택 사항)
@Setting(settingPath = "elasticsearch/document-settings.json") // 설정 파일 경로 (선택 사항)
@ToString
public class WikiPageDocument {

    // 통합 검색을 위한 타입 상수 정의
    public static final String DOCUMENT_TYPE = "WIKIPAGE";

    @Id
    @Field(type = FieldType.Keyword) // 정확한 일치 검색
    private String wikiId;

    @Field(
            type = FieldType.Text,
            analyzer = "autocomplete_analyzer", // 자동완성 분석기
            searchAnalyzer = "korean_analyzer" // 검색 시 한국어 분석기
    )
    private String title;

    @Field(type = FieldType.Keyword) // 현재 리비전 (정확한 일치)
    private String currentRevision;

    @Field(type = FieldType.Keyword) // Enum 값을 문자열 키워드로 저장
    private String wikiStatus;

    @Field(type = FieldType.Keyword) // Enum 값을 문자열 키워드로 저장
    private String wikiClass;

    @Field(type = FieldType.Keyword) // 연결된 아티스트 ID
    private String artistId;

    @Field(type = FieldType.Keyword) // 연결된 앨범 ID
    private String albumId;

    @Field(type = FieldType.Keyword) // 연결된 장비 ID
    private String gearId;

    // JPA 엔티티를 Document로 변환하는 헬퍼 메서드
    public static WikiPageDocument from(WikiPage wikiPage) {
        Artist artist = wikiPage.getArtist();
        Album album = wikiPage.getAlbum();
        Gear gear = wikiPage.getGear();

        // Artist, Album, Gear 엔티티의 ID getter 메서드명으로 변경해야 합니다.
        String artistId = (artist != null) ? artist.getArtistId() : null; // 예: artist.getArtistId()
        String albumId = (album != null) ? album.getAlbumId() : null; // 예: album.getAlbumId()
        String gearId = (gear != null) ? gear.getGearId() : null; // 예: gear.getGearId()

        return WikiPageDocument.builder()
                .wikiId(wikiPage.getWikiId())
                .title(wikiPage.getTitle())
                .currentRevision(wikiPage.getCurrentRevision())
                .wikiStatus(wikiPage.getWikiStatus().name()) // Enum -> String
                .wikiClass(wikiPage.getWikiClass().name()) // Enum -> String
                .artistId(artistId)
                .albumId(albumId)
                .gearId(gearId)
                .build();
    }
}
