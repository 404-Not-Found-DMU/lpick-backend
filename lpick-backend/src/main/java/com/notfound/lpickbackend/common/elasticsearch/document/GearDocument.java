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

    // Gear 엔티티에는 ID 필드가 없으므로, @Id로 사용할 필드 (예: name + modelName 조합 또는 내부 ID)가 필요합니다.
    // JPA 엔티티를 보니 BaseEntity를 상속받았는데, ID를 추정하기 어려워 name을 @Id로 사용하겠습니다.
    // **실제 Gear 엔티티의 @Id 필드를 사용하도록 수정해야 합니다.**
    @Id
    @Field(type = FieldType.Keyword)
    private String name; // 임시 ID (실제 @Id 필드로 변경 필요)

    @Field(
            type = FieldType.Text,
            analyzer = "autocomplete_analyzer",
            searchAnalyzer = "korean_analyzer"
    )
    private String modelName; // 기어 검색 시 주요 대상

    @Field(type = FieldType.Keyword)
    private String brand;

    @Field(type = FieldType.Keyword)
    private String eqClass; // GearClass의 ID나 이름을 저장한다고 가정

    @Field(type = FieldType.Keyword)
    private String wikiId;

    public static GearDocument from(Gear gear) {
        // Gear 엔티티에 getGearId()가 없으므로 name을 ID로 사용했습니다. 실제 ID 필드로 수정하세요.
        return GearDocument.builder()
                .name(gear.getName())
                .modelName(gear.getModelName())
                .brand(gear.getBrand())
                .eqClass(gear.getEqClass() != null ? gear.getEqClass().getClassName() : null) // GearClass 이름을 저장한다고 가정
                .wikiId(gear.getWiki() != null ? gear.getWiki().getWikiId() : null)
                .build();
    }
}
