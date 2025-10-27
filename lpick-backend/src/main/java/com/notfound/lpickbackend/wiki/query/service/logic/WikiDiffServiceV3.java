package com.notfound.lpickbackend.wiki.query.service.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/** JsonNode 로 저장된 content를 비교하는 버전의 service 코드 */
@Service
@RequiredArgsConstructor
public class WikiDiffServiceV3 {
    /** jsonb content(ObjectNode) → 사람 읽기 쉬운 라인 문자열 */
    public static String linearizeForDiff(ObjectNode root) {
        StringBuilder sb = new StringBuilder(8192);

        // 0) 공통 헤더
        String type = root.at("/categoryData/type").asText("").toLowerCase(Locale.ROOT);
        sb.append("# TYPE: ").append(type).append('\n');

        // 1) 타입별 요약(메타) 라인
        appendTypeSummaryLines(sb, type, root);

        // 2) 텍스트 블록들
        appendTextBlocks(sb, root);

        return sb.toString();
    }

    private static void appendTypeSummaryLines(StringBuilder sb, String type, ObjectNode root) {
        switch (type) {
            case "equipment" -> {
                JsonNode d = root.at("/categoryData/data");
                line(sb, "EQUIP.NAME", d.path("name").asText(""));
                line(sb, "EQUIP.BRAND", d.path("brand").asText(""));
                line(sb, "EQUIP.YEAR", d.path("releaseYear").asText(""));
                line(sb, "EQUIP.TYPE", d.path("equipmentType").asText(""));
                line(sb, "EQUIP.IMG",  d.path("imageUrl").asText(""));
            }
            case "artist" -> {
                JsonNode d = root.at("/categoryData/data");
                line(sb, "ARTIST.NAME", d.path("name").asText(""));
                line(sb, "ARTIST.COUNTRY", d.path("country").asText(""));
                line(sb, "ARTIST.PERIOD", d.path("activePeriod").asText(""));
                // roles
                for (JsonNode r : d.path("roles")) line(sb, "ARTIST.ROLE", r.asText(""));
                // discography
                int idx = 1;
                for (JsonNode it : asArray(d.path("discography"))) {
                    line(sb, "DISCO." + idx + ".TITLE", it.path("title").asText(""));
                    line(sb, "DISCO." + idx + ".DATE",  it.path("releaseDate").asText(""));
                    line(sb, "DISCO." + idx + ".TYPE",  it.path("type").asText(""));
                    line(sb, "DISCO." + idx + ".ROLE",  it.path("role").asText(""));
                    idx++;
                }
            }
            case "lp" -> {
                JsonNode info = root.at("/categoryData/data/infobox");
                line(sb, "LP.TITLE",  info.path("title").asText(""));
                line(sb, "LP.ARTIST", info.path("artist").asText(""));
                line(sb, "LP.DATE",   info.path("releaseDate").asText(""));
                line(sb, "LP.GENRE",  info.path("genre").asText(""));
                line(sb, "LP.LABEL",  info.path("label").asText(""));

                // 트랙 요약 (이름/길이)
                int i = 1;
                for (JsonNode t : root.at("/categoryData/data/tracklist/tracks")) {
                    line(sb, "TRACK."+i, t.path("number").asText("") + " | " +
                            t.path("title").asText("")  + " | " +
                            t.path("length").asText(""));
                    i++;
                }
            }
            case "other" -> {
                JsonNode d = root.at("/categoryData/data");
                line(sb, "OTHER.TITLE",   d.path("title").asText(""));
                line(sb, "OTHER.SUMMARY", firstLine(d.path("content").asText("")));
            }
            default -> { /* unknown type → 아무 것도 추가 안 함 */ }
        }
        sb.append('\n'); // 메타와 본문 사이 공백
    }

    private static void appendTextBlocks(StringBuilder sb, ObjectNode root) {
        JsonNode blocks = root.path("textBlocks");
        if (!(blocks instanceof ArrayNode arr)) return;

        int fallbackOrder = 0;
        for (JsonNode n : arr) {
            if (!(n instanceof ObjectNode o)) continue;

            String id     = o.path("id").asText("");
            String title  = o.path("title").asText("");
            String content= normalizeEol(o.path("content").asText(""));
            int depth     = o.path("depth").asInt(0);
            int order     = o.path("order").canConvertToInt()
                    ? o.path("order").asInt()
                    : fallbackOrder++;

            // 블록 헤더(한 줄)
            sb.append("@@BLOCK ")
                    .append("id=").append(id).append(' ')
                    .append("order=").append(order).append(' ')
                    .append("depth=").append(depth).append(' ')
                    .append("title=").append(escapeMarker(title))
                    .append('\n');

            // 본문
            sb.append(content).append('\n');

            // 블록 종료 마커(파서가 필요하면 활용)
            sb.append("@@END").append('\n');
        }
    }

    private static void line(StringBuilder sb, String key, String val) {
        sb.append(key).append(": ").append(val == null ? "" : val).append('\n');
    }

    private static String firstLine(String s) {
        int i = s.indexOf('\n');
        return i < 0 ? s : s.substring(0, i);
    }

    private static List<JsonNode> asArray(JsonNode n) {
        List<JsonNode> out = new ArrayList<>();
        if (n instanceof ArrayNode arr) arr.forEach(out::add);
        return out;
    }

    private static String normalizeEol(String s) {
        return s.replace("\r\n", "\n").replace("\r", "\n");
    }

    private static String escapeMarker(String s) {
        if (s == null) return "";
        return s.replace("\n", "\\n");
    }
}
