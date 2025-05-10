package com.notfound.lpickbackend.Wiki.Query.Service;

import com.notfound.lpickbackend.AUTO_ENTITIES.PageRevision;
import lombok.AllArgsConstructor;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Operation;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Diff;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@AllArgsConstructor
public class WikiDiffServiceV2 {
    private final PageRevisionService pageRevisionService;
    private final DiffMatchPatch dmp = new DiffMatchPatch();


    public String getTwoRevisionDiffHtml(String wikiId, String oldVersion, String newVersion) {
        List<PageRevision> pageRevisionList = pageRevisionService.getTwoRevision(wikiId, oldVersion, newVersion);

        String oldContent = pageRevisionList.get(0).getContent();
        String newContent = pageRevisionList.get(1).getContent();

        return generateDiffHtml(oldContent, newContent, oldVersion, newVersion);
    }


    public String generateDiffHtml(String a, String b, String labelA, String labelB) {
        if (a.equals(b)) {
            return "";
        }

        // 1) diff 생성 및 정리
        LinkedList<Diff> diffs = dmp.diffMain(a, b);
        dmp.diffCleanupSemantic(diffs);

        // 2) 마지막 줄 flush
        diffs.add(new Diff(Operation.EQUAL, "\n"));

        // 3) 라인 단위로 분할
        List<LineDiff> lineDiffs = parseLineDiffs(diffs);

        // 4) HTML 테이블 빌드
        StringBuilder sb = new StringBuilder();
        sb.append("<table style=\"width:100%; white-space:pre-wrap;\">")
                .append("<tr><td colspan=\"2\">")
                .append(labelA).append(" ➤ ").append(labelB)
                .append("</td></tr><tr><td style=\"width:40px; user-select:none;\">");

        int currentLine = 0;
        for (LineDiff ld : lineDiffs) {
            if (currentLine != ld.getLine()) {
                currentLine = ld.getLine();
                sb.append("</td></tr><tr><td style=\"width:40px; user-select:none;\">")
                        .append(currentLine)
                        .append("</td><td>");
            }
            String text = escapeHtml(ld.getText());
            if (ld.getOp() == Operation.INSERT) {
                sb.append("<span class=\"opennamu_diff_green\">").append(text).append("</span>");
            } else if (ld.getOp() == Operation.DELETE) {
                sb.append("<span class=\"opennamu_diff_red\">").append(text).append("</span>");
            } else {
                sb.append(text);
            }
        }

        sb.append("</td></tr></table>");
        return sb.toString();
    }

    private List<LineDiff> parseLineDiffs(List<Diff> diffs) {
        List<LineDiff> result = new LinkedList<>();
        int line = 1;
        boolean pendingChange = false;

        for (Diff diff : diffs) {
            String[] parts = diff.text.split("(?<=\\n)");
            for (String part : parts) {
                boolean isLineEnd = part.endsWith("\n");
                String content   = isLineEnd ? part.substring(0, part.length() - 1) : part;

                if (diff.operation != Operation.EQUAL) {
                    pendingChange = true;
                }

                if (pendingChange) {
                    result.add(new LineDiff(line, diff.operation, content));
                }
                if (isLineEnd) {
                    line++;
                    pendingChange = false;
                }
            }
        }

        return result;
    }

    private String escapeHtml(String input) {
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    private static class LineDiff {
        private final int line;
        private final Operation op;
        private final String text;

        LineDiff(int line, Operation op, String text) {
            this.line = line;
            this.op   = op;
            this.text = text;
        }
        public int getLine()   { return line; }
        public Operation getOp() { return op; }
        public String getText() { return text; }
    }
}