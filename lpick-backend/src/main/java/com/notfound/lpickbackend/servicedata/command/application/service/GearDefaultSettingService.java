package com.notfound.lpickbackend.servicedata.command.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.servicedata.command.application.domain.Gear;
import com.notfound.lpickbackend.servicedata.command.application.domain.GearClass;
import com.notfound.lpickbackend.servicedata.command.application.repository.GearClassCommandRepository;
import com.notfound.lpickbackend.servicedata.command.application.repository.GearDefaultSettingRepository;
import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.GearClassEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class GearDefaultSettingService {
    private final GearDefaultSettingRepository gearRepository;
    private final GearClassCommandRepository gearClassRepository;
    private final ObjectMapper objectMapper;

    private static final List<String> DEFAULT_SPEC_KEYS = List.of(
            "Type",
            "Drive",
            "Speeds Supported",
            "Pitch Control",
            "Speed Control",
            "Wow And Flutter",
            "Signal To Noise Ratio",
            "Rumble",
            "Platter Material",
            "Platter Weight",
            "Tonearm Type",
            "Tonearm Effective Length",
            "Tracking Force Range",
            "Cartridge Weight Range",
            "Dimensions",
            "Weight"
    );
    private static final String NONE = "None";

    public Result importFlatCsv(MultipartFile csvFile, boolean dryRun, GearClassEnum gearClass) throws Exception {
        // 1) 항상 TURNTABLE로 연결 (GearClass PK = className)
        GearClass eqClass = gearClassRepository.findById(gearClass.name())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_GEAR_CLASS));

        // 2) CSV 파싱 + 제네릭 고정
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        ObjectReader reader = csvMapper
                .readerFor(new TypeReference<Map<String, String>>() {})
                .with(schema);

        int inserted = 0, updated = 0, skipped = 0, total = 0;

        try (InputStream is = csvFile.getInputStream();
             MappingIterator<Map<String, String>> it = reader.readValues(is)) {

            while (it.hasNext()) {
                total++;
                Map<String, String> r = it.next();

                String name      = safe(r.get("name"));
                String modelName = cut(safe(r.get("model_name")), 100);
                String brand     = cut(safe(r.get("brand")), 50);
                boolean isTemp   = parseBool(r.get("is_temp"));
                String metaJson  = safe(r.get("meta_json"));

                if (brand.isBlank() || modelName.isBlank() || metaJson.isBlank()) {
                    skipped++;
                    continue;
                }

                JsonNode rawMeta;
                try {
                    rawMeta = objectMapper.readTree(metaJson);
                } catch (Exception e) {
                    skipped++;
                    continue;
                }

                // 3) meta 클린업 + 필수 스펙 키 채움 + img 추출
                CleanResult cr = cleanupAndFill(rawMeta);
                String imgUrl = cr.img();

                Optional<Gear> existingOpt = gearRepository.findByBrandAndModelName(brand, modelName);

                if (dryRun) {
                    if (existingOpt.isPresent()) updated++; else inserted++;
                    continue;
                }

                Gear gear = existingOpt.orElseGet(Gear::new);
                gear.setName(ellipsis(name, 50));
                gear.setModelName(modelName);
                gear.setBrand(brand);
                gear.setTemp(isTemp);
                gear.setEqClass(eqClass);      // 항상 TURNTABLE
                gear.setImg(imgUrl);           // 새 컬럼 설정
                // meta는 Map으로 저장(엔티티 매핑 타입에 맞춰 변환)
                JsonNode cleanedMeta = cr.meta();   // cr.meta()는 ObjectNode(JsonNode 하위)
                gear.setMeta(cleanedMeta);

                gearRepository.save(gear);

                if (existingOpt.isPresent()) updated++; else inserted++;
            }
        }

        return new Result(inserted, updated, skipped, total, dryRun);
    }

    /** meta에서 brand/specs_raw 제거, specs에 디폴트 키 채움, 첫 이미지 추출 */
    private CleanResult cleanupAndFill(JsonNode rawMeta) {
        ObjectNode meta = objectMapper.createObjectNode();

        // 허용 필드만 복사 (brand/specs_raw는 복사하지 않음)
        copyIfExists(rawMeta, meta, "source");
        copyIfExists(rawMeta, meta, "source_captured_at");
        copyIfExists(rawMeta, meta, "category");
        copyIfExists(rawMeta, meta, "explain");
        copyIfExists(rawMeta, meta, "image_url"); // 추적용으로 남겨둬도 무방

        // specs 확보
        ObjectNode specs = objectMapper.createObjectNode();
        if (rawMeta.has("specs") && rawMeta.get("specs").isObject()) {
            specs.setAll((ObjectNode) rawMeta.get("specs"));
        }
        // 디폴트 키 채움
        for (String key : DEFAULT_SPEC_KEYS) {
            if (!specs.has(key) || specs.get(key).asText("").isBlank()) {
                specs.put(key, NONE);
            }
        }
        meta.set("specs", specs);

        // 이미지 추출 (image_url | image | images …)
        String imgUrl = extractFirstImageUrl(rawMeta);

        return new CleanResult(meta, imgUrl);
    }

    private static void copyIfExists(JsonNode src, ObjectNode dst, String field) {
        if (src.has(field)) dst.set(field, src.get(field));
    }

    private static String extractFirstImageUrl(JsonNode meta) {
        // 우선순위: image_url(string) → image(string/array) → images(array)
        if (meta.has("image_url") && meta.get("image_url").isTextual()) {
            String s = meta.get("image_url").asText();
            if (!s.isBlank()) return s;
        }
        if (meta.has("image")) {
            JsonNode n = meta.get("image");
            if (n.isTextual()) {
                String s = n.asText();
                if (!s.isBlank()) return s;
            } else if (n.isArray()) {
                for (JsonNode e : n) {
                    if (e.isTextual() && !e.asText().isBlank()) return e.asText();
                }
            }
        }
        if (meta.has("images") && meta.get("images").isArray()) {
            for (JsonNode e : meta.get("images")) {
                if (e.isTextual() && !e.asText().isBlank()) return e.asText();
            }
        }
        return null;
    }

    // ==== helpers ====
    public record Result(int inserted, int updated, int skipped, int total, boolean dryRun) {}
    private record CleanResult(ObjectNode meta, String img) {}

    private static String safe(String s) { return s == null ? "" : s.trim(); }
    private static boolean parseBool(String s) { return "true".equalsIgnoreCase(safe(s)); }
    private static String cut(String s, int max) { return (s.length() > max) ? s.substring(0, max) : s; }
    private static String ellipsis(String s, int max) {
        if (s.length() <= max) return s;
        return (max <= 3) ? s.substring(0, max) : s.substring(0, max - 3) + "...";
    }

}