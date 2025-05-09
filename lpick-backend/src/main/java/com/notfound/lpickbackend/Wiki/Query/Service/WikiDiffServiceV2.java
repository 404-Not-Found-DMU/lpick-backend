package com.notfound.lpickbackend.Wiki.Query.Service;

import name.fraser.neil.plaintext.diff_match_patch;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WikiDiffServiceV2 {

    private final diff_match_patch dmp = new diff_match_patch();

    public String generateDiffHtml(String a, String b, String labelA, String labelB) {
        if (a.equals(b)) {
            return "";
        }

        List<diff_match_patch.Diff> diffs = dmp.diff_main(a, b);
        dmp.diff_cleanupSemantic(diffs);

        // Append newline to flush last line
        diffs.add(new diff_match_patch.Diff(diff_match_patch.Operation.EQUAL, "\n"));

        // Break into line-based segments
        List<LineDiff> lineDiffs = parseLineDiffs(diffs);

        // Build HTML
        StringBuilder sb = new StringBuilder();
        sb.append("<table style=\"width:100%; white-space:pre-wrap;\">")
                .append("<tr><td colspan=\"2\">" + labelA + " ➤ " + labelB + "</td></tr>")
                .append("<tr><td style=\"width:40px; user-select:none;\">");

        int currentLine = 0;
        for (LineDiff ld : lineDiffs) {
            if (currentLine != ld.getLine()) {
                currentLine = ld.getLine();
                sb.append("</td></tr><tr><td style=\"width:40px; user-select:none;\">" + currentLine + "</td><td>");
            }
            switch (ld.getOp()) {
                case INSERT:
                    sb.append("<span class=\"opennamu_diff_green\">" + escapeHtml(ld.getText()) + "</span>");
                    break;
                case DELETE:
                    sb.append("<span class=\"opennamu_diff_red\">" + escapeHtml(ld.getText()) + "</span>");
                    break;
                default:
                    sb.append(escapeHtml(ld.getText()));
            }
        }
        sb.append("</td></tr></table>");
        return sb.toString();
    }

    private List<LineDiff> parseLineDiffs(List<diff_match_patch.Diff> diffs) {
        List<LineDiff> result = new LinkedList<>();
        int line = 1;
        boolean pendingChange = false;

        for (diff_match_patch.Diff diff : diffs) {
            String text = diff.text;
            String[] parts = text.split("(?<=\\n)"); // keep newline
            for (String part : parts) {
                boolean isLineEnd = part.endsWith("\n");
                String content = isLineEnd ? part.substring(0, part.length()-1) : part;

                if (diff.operation != diff_match_patch.Operation.EQUAL) {
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
        private final diff_match_patch.Operation op;
        private final String text;

        LineDiff(int line, diff_match_patch.Operation op, String text) {
            this.line = line;
            this.op = op;
            this.text = text;
        }
        public int getLine() { return line; }
        public diff_match_patch.Operation getOp() { return op; }
        public String getText() { return text; }
    }
}