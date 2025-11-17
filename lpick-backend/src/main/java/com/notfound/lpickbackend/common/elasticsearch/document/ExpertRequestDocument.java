package com.notfound.lpickbackend.common.elasticsearch.document;

import com.notfound.lpickbackend.servicedata.command.application.domain.inherenceENUM.MusicGenre;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.ExpertRequest;
import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.ExpertRequestStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@Document(indexName = "expert_request")
@Mapping(mappingPath = "elasticsearch/expertrequest-mapping.json")
@Setting(settingPath = "elasticsearch/documents-settings-by-gear.json") //  NN% 형식 검색으로 구현
@ToString
public class ExpertRequestDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private String expertRequestId;

    @Field(
            type = FieldType.Text,
            analyzer = "autocomplete_analyzer",
            searchAnalyzer = "korean_analyzer"
    )
    private String name;

    @Field(
            type = FieldType.Text,
            analyzer = "autocomplete_analyzer",
            searchAnalyzer = "korean_analyzer"
    )
    private String email;

    // 영어 ENUM 코드 문자열들 (예: ["ROCK_MUSIC","METAL"])
    @Field(type = FieldType.Keyword)
    private List<MusicGenre> musicGenre;

    // 한글 표기들 (예: ["락","메탈"])
    @Field(
            type = FieldType.Text,
            analyzer = "autocomplete_analyzer",
            searchAnalyzer = "korean_analyzer"
    )
    private List<String> musicGenresKo;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private Instant createdAt;

    @Field(type = FieldType.Keyword)
    private String expertRequestStatus;

    public static ExpertRequestDocument from(ExpertRequest er) {

        List<MusicGenre> genres = er.getMusicGenre(); // 엔티티의 List<MusicGenre>

        List<String> genresKo = genres.stream()
                .map(MusicGenre::getKoLabel) // 위에서 만든 한글 라벨 사용
                .toList();

        return ExpertRequestDocument.builder()
                .expertRequestId(er.getExpertRequestId())
                .name(er.getName())
                .email(er.getEmail())
                .musicGenre(genres)
                .musicGenresKo(genresKo)
                .createdAt(er.getCreatedAt())
                .expertRequestStatus(er.getStatus().name())
                .build();
    }
}
