package com.notfound.lpickbackend.wiki.revision_domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class WikiSchema {
    private final JsonSchema schema;

    public WikiSchema() {
        try {
            var res = new ClassPathResource("revision_schema/wiki.schema.json");
            var bytes = res.getInputStream().readAllBytes();
            var schemaStr = new String(bytes, StandardCharsets.UTF_8);

            var factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
            this.schema = factory.getSchema(schemaStr);
        } catch (Exception e) {
            throw new IllegalStateException("JSON 스키마 로드 실패", e);
        }
    }

    public void validateOrThrow(JsonNode node) {
        var errors = schema.validate(node);
        if (!errors.isEmpty()) {
            var sb = new StringBuilder("JSON Schema 위반:\n");
            for (var err : errors) {
                sb.append("- ").append(err.getMessage()).append('\n');
            }
            throw new IllegalArgumentException(sb.toString());
        }
    }
}

