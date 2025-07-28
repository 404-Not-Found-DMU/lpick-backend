package com.notfound.lpickbackend.userinfo.query.dto.response.usergear;

import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.GearClass;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GearInfoResponse {
    private String id;
    private String name;
    private String modelName;
    private String brand;
    private GearClass gearClass;
    private String wikiId;
    
    // 쿼리로 구현한 GearInfoResponse의 gearClass를 String으로 전달받아 Java 단에서 Enum으로 변경하기위한 목적의 별도 생성자
    public GearInfoResponse(String id, String name, String modelName, String brand, String gearClass, String wikiId) {
        this.id = id;
        this.name = name;
        this.modelName = modelName;
        this.brand = brand;
        this.gearClass = GearClass.valueOf(gearClass.toUpperCase());
        this.wikiId = wikiId;
    }
    
    // AllArgsConstructor - 빌더 사용 목적
    public GearInfoResponse(String id, String name, String modelName, String brand, GearClass gearClass, String wikiId) {
        this.id = id;
        this.name = name;
        this.modelName = modelName;
        this.brand = brand;
        this.gearClass = gearClass;
        this.wikiId = wikiId;
    }
}
