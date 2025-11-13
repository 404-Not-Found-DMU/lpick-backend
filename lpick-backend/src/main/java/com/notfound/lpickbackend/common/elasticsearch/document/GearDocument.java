package com.notfound.lpickbackend.common.elasticsearch.document;

import com.notfound.lpickbackend.servicedata.command.application.domain.Gear;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@Builder
@Document(indexName = "gears")
@Mapping(mappingPath = "elasticsearch/gear-mapping.json")
@Setting(settingPath = "elasticsearch/document-settings.json")
@ToString
public class GearDocument {

    // 통합 검색을 위한 타입 상수 정의
    public static final String DOCUMENT_TYPE = "GEAR";


    // BaseEntity를 제거하고 다음과 같이 gearId 추가하였음.
    // 이제 Gear는 상속과 관계없이 자체적인 Id를 소유함.
    @Id
    @Field(type = FieldType.Keyword)
    private String gearId;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(
            type = FieldType.Text,
            analyzer = "autocomplete_analyzer",
            searchAnalyzer = "korean_analyzer"
    )
    private String modelName; // 기어 검색 시 주요 대상

    @Field(type = FieldType.Keyword)
    private String brand;

    @Field(type = FieldType.Keyword)
    private String eqClass; // GearClass 이름을 저장 - TURNTABLE, SPEAKER, HEADPHONE

    @Field(type = FieldType.Keyword)
    private String wikiId;

    public static GearDocument from(Gear gear) {
        // Gear 엔티티에 getGearId()가 없으므로 name을 ID로 사용했습니다. 실제 ID 필드로 수정하세요.
        return GearDocument.builder()
                .gearId(gear.getGearId())
                .name(gear.getName())
                .modelName(gear.getModelName())
                .brand(gear.getBrand())
                .eqClass(gear.getEqClass() != null ? gear.getEqClass().getClassName() : null) // GearClass 이름을 저장한다고 가정
                .wikiId(gear.getWiki() != null ? gear.getWiki().getWikiId() : null)
                .build();
    }
}
