package com.notfound.lpickbackend.common.json_schema_draft;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.GearClassEnum;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/** Gear가 소유하는 meta 컬럼 내의 specs에 대한 스키마 설정. json 내에 각각의 분류별로 15가지 스펙은 반드시 존재해야한다.(없으면 None 문자열 처리) */
@Component
public class GearSpecSchema {
    private final Map<String, JsonSchema> specSchemas = new HashMap<>();

    public GearSpecSchema() {
        var factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);

        for (GearClassEnum gearClass : GearClassEnum.values()) {
            String typeName = gearClass.name();  // e.g., TURNTABLE, SPEAKER, HEADPHONE
            String schemaPath = String.format("revision_schema/specs_schema_%s.json", typeName.toLowerCase());

            try (var in = new ClassPathResource(schemaPath).getInputStream()) {
                var schemaStr = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                specSchemas.put(typeName, factory.getSchema(schemaStr));
            } catch (Exception e) {
                throw new IllegalStateException("스키마 파일 로드 실패: " + schemaPath, e);
            }
        }
    }

    public void validateSpecsOrThrow(JsonNode meta, String eqClassName) {
        var specs = meta.get("specs");
        if (specs == null || specs.isNull()) {
            throw new IllegalArgumentException("meta 내에 specs 항목이 없습니다.");
        }

        JsonSchema schema = specSchemas.get(eqClassName.toUpperCase());
        if (schema == null) {
            throw new IllegalArgumentException("지원되지 않는 GearClass: " + eqClassName);
        }

        var errors = schema.validate(specs);
        if (!errors.isEmpty()) {
            var sb = new StringBuilder("Gear specs JSON Schema 위반:\n");
            for (var err : errors) {
                sb.append("- ").append(err.getMessage()).append('\n');
            }
            throw new IllegalStateException(sb.toString());
        }
    }
}
