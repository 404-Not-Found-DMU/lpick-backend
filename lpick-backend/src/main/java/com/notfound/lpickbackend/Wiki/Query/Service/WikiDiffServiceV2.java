package com.notfound.lpickbackend.Wiki.Query.Service;

import com.notfound.lpickbackend.AUTO_ENTITIES.PageRevision;
import lombok.AllArgsConstructor;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Operation;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Diff;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class WikiDiffServiceV2 {
    private final PageRevisionQueryService pageRevisionQueryService;
    private final DiffMatchPatch dmp = new DiffMatchPatch();


    public String getTwoRevisionDiffHtml(String wikiId, String oldVersion, String newVersion) {
        List<PageRevision> pageRevisionList = pageRevisionQueryService.getTwoRevision(wikiId, oldVersion, newVersion);

        if(pageRevisionList.size() < 2) throw new IllegalArgumentException("리비전 비교대상이 정상적으로 불러와지지않음.");

        String oldContent = pageRevisionList.get(0).getContent();
        String newContent = pageRevisionList.get(1).getContent();

        return generateDiffHtml(oldContent, newContent, oldVersion, newVersion);
    }

    /**
     * oldText vs newText 의 풀-컨텍스트 diff.
     * 왼쪽에 old 리비전 라인 번호, 중간에 new 리비전 라인 번호,
     * 오른쪽에 텍스트(unchanged, 삭제=빨강, 삽입=초록)를 한 행에 모두 모아 출력.
     */
    public String generateDiffHtml(
            String oldText,
            String newText,
            String labelOld,
            String labelNew
    ) {
        if (oldText.equals(newText)) {
            return "";
        }

        // 1) diff 생성 + semantic cleanup
        LinkedList<Diff> diffs = dmp.diffMain(oldText, newText);
        dmp.diffCleanupSemantic(diffs);
        diffs.add(new Diff(Operation.EQUAL, "\n"));   // 마지막 줄 강제 개행

        // 2) (oldLine,newLine) 키로 그룹핑
        List<CombinedLine> rows = groupByLine(diffs);

        // 3) HTML 빌드
        StringBuilder sb = new StringBuilder();
        sb.append("<table class=\"diff-table\" style=\"white-space:pre-wrap;\">")
                .append("<tr><th>old</th><th>new</th><th>")
                .append(labelOld).append(" ➤ ").append(labelNew)
                .append("</th></tr>");

        for (CombinedLine row : rows) {
            boolean hasDel = row.segments.stream()
                    .anyMatch(s -> s.op == Operation.DELETE);
            boolean hasIns = row.segments.stream()
                    .anyMatch(s -> s.op == Operation.INSERT);

            // — 삭제(old) 행 —
            if (hasDel) {
                sb.append("<tr>")
                        .append("<td>").append(row.oldLine).append("</td>")
                        .append("<td></td>")
                        .append("<td>");
                for (Segment s : row.segments) {
                    if (s.op == Operation.INSERT) continue;
                    String esc = escapeHtml(s.text);
                    if (s.op == Operation.DELETE) {
                        sb.append("<span class=\"opennamu_diff_red\">")
                                .append(esc).append("</span>");
                    } else { // EQUAL
                        sb.append(esc);
                    }
                }
                sb.append("</td></tr>");
            }

            // — 추가(new) 행 —
            if (hasIns) {
                sb.append("<tr>")
                        .append("<td></td>")
                        .append("<td>").append(row.newLine).append("</td>")
                        .append("<td>");
                for (Segment s : row.segments) {
                    if (s.op == Operation.DELETE) continue;
                    String esc = escapeHtml(s.text);
                    if (s.op == Operation.INSERT) {
                        sb.append("<span class=\"opennamu_diff_green\">")
                                .append(esc).append("</span>");
                    } else { // EQUAL
                        sb.append(esc);
                    }
                }
                sb.append("</td></tr>");
            }

            // — 변경 없는 행 (only EQUAL) —
            if (!hasDel && !hasIns) {
                sb.append("<tr>")
                        .append("<td>").append(row.oldLine).append("</td>")
                        .append("<td>").append(row.newLine).append("</td>")
                        .append("<td>");
                for (Segment s : row.segments) {
                    sb.append(escapeHtml(s.text));
                }
                sb.append("</td></tr>");
            }
        }

        sb.append("</table>");
        return sb.toString();
    }

    /** (oldLine,newLine) 키로 Diff 조각들을 한 줄 단위로 묶습니다. */
    private List<CombinedLine> groupByLine(List<Diff> diffs) {
        Map<String, CombinedLine> map = new LinkedHashMap<>();
        int oldLine = 1, newLine = 1;

        for (Diff diff : diffs) {
            for (String part : diff.text.split("(?<=\\n)")) {
                boolean endsNL = part.endsWith("\n");
                String content = endsNL
                        ? part.substring(0, part.length() - 1)
                        : part;

                // 증가 전 카운터를 사용
                int ol = oldLine, nl = newLine;
                String key = ol + ":" + nl;
                CombinedLine cl = map.computeIfAbsent(key, k -> new CombinedLine(ol, nl));
                cl.segments.add(new Segment(diff.operation, content));

                // 개행이 있으면 카운터만 증가 (old/new 동시 혹은 분리)
                if (endsNL) {
                    if (diff.operation == Operation.EQUAL) {
                        oldLine++; newLine++;
                    } else if (diff.operation == Operation.DELETE) {
                        oldLine++;
                    } else if (diff.operation == Operation.INSERT) {
                        newLine++;
                    }
                }
            }
        }
        return new ArrayList<>(map.values());
    }

    /** HTML 이스케이프 */
    private String escapeHtml(String s) {
        return s.replace("&","&amp;")
                .replace("<","&lt;")
                .replace(">","&gt;")
                .replace("\"","&quot;")
                .replace("'","&#x27;");
    }

    /** 한 행(row)을 대표하는 DTO */
    private static class CombinedLine {
        final int oldLine, newLine;
        final List<Segment> segments = new ArrayList<>();
        CombinedLine(int ol, int nl) { this.oldLine = ol; this.newLine = nl; }
    }

    /** 줄 내부 diff 조각 */
    private static class Segment {
        final Operation op;
        final String text;
        Segment(Operation op, String text) { this.op = op; this.text = text; }
    }
}