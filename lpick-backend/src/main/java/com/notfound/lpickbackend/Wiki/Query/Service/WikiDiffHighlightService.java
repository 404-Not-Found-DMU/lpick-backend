package com.notfound.lpickbackend.Wiki.Query.Service;

import com.notfound.lpickbackend.Wiki.Command.Application.DTO.DiffWord;
import com.notfound.lpickbackend.Wiki.Enum.DiffOp;
import com.notfound.lpickbackend.Wiki.Query.DTO.HighLight;
import com.notfound.lpickbackend.Wiki.Query.DTO.HighLightedDiffLine;
import com.notfound.lpickbackend.Wiki.Query.Enum.HighLightType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class WikiDiffHighlightService {

    public List<HighLightedDiffLine> computeHighlightedDiff(
            List<String> oldVersion,
            List<String> newVersion) {

        // 0) 공백 정규화된 비교용 리스트
        List<String> a = oldVersion.stream()
                .map(this::normalize)
                .toList();
        List<String> b = newVersion.stream()
                .map(this::normalize)
                .toList();
        int N = a.size(), M = b.size();

        // 1) 공통 prefix 길이 계산
        int prefix = 0;
        while (prefix < N && prefix < M && a.get(prefix).equals(b.get(prefix))) {
            prefix++;
        }

        // 2) 공통 suffix 길이 계산
        int suffix = 0;
        while (suffix < N - prefix && suffix < M - prefix
                && a.get(N - 1 - suffix).equals(b.get(M - 1 - suffix))) {
            suffix++;
        }

        List<HighLightedDiffLine> result = new ArrayList<>();
        int i = 0, j = 0;

        // 3) prefix EQUAL 처리
        for (; i < prefix && j < prefix; i++, j++) {
            String line = oldVersion.get(i);
            result.add(new HighLightedDiffLine(
                    DiffOp.EQUAL,
                    i + 1,
                    j + 1,
                    line,
                    line,
                    Collections.emptyList(),
                    Collections.emptyList()
            ));
        }

        // 4) 중간 구간 diff
        int endOld = N - suffix;
        int endNew = M - suffix;
        if (prefix < endOld || prefix < endNew) {
            List<String> subA = a.subList(prefix, endOld);
            List<String> subB = b.subList(prefix, endNew);
            List<Edit> script = buildEditScript(subA, subB);

            for (int idx = 0; idx < script.size(); idx++) {
                Edit e = script.get(idx);
                int oldIdx = prefix + e.oldIndex;
                int newIdx = prefix + e.newIndex;

                // 1:1 교체 감지 (REMOVE 다음 ADD)
                if (e.type == Edit.Type.REMOVE
                        && idx + 1 < script.size()
                        && script.get(idx + 1).type == Edit.Type.ADD) {

                    Edit add = script.get(idx + 1);
                    int addNewIdx = prefix + add.newIndex;
                    String oldLine = oldVersion.get(oldIdx);
                    String newLine = newVersion.get(addNewIdx);

                    List<DiffWord> charDiff = computeCharLevelDiff(oldLine, newLine);
                    List<HighLight> oldHL = extractHighlights(charDiff, HighLightType.REMOVE);
                    List<HighLight> newHL = extractHighlights(charDiff, HighLightType.ADD);

                    result.add(new HighLightedDiffLine(
                            DiffOp.REPLACE,
                            oldIdx + 1,
                            addNewIdx + 1,
                            oldLine,
                            newLine,
                            oldHL,
                            newHL
                    ));
                    idx++;  // ADD 스텝 건너뛰기
                    i = oldIdx + 1;
                    j = addNewIdx + 1;
                }
                // 단순 삭제
                else if (e.type == Edit.Type.REMOVE) {
                    String oldLine = oldVersion.get(oldIdx);
                    List<DiffWord> charDiff = computeCharLevelDiff(oldLine, "");
                    List<HighLight> oldHL = extractHighlights(charDiff, HighLightType.REMOVE);

                    result.add(new HighLightedDiffLine(
                            DiffOp.REMOVE,
                            oldIdx + 1,
                            null,
                            oldLine,
                            "",
                            oldHL,
                            Collections.emptyList()
                    ));
                    i = oldIdx + 1;
                }
                // 단순 추가
                else {  // ADD
                    String newLine = newVersion.get(newIdx);
                    List<DiffWord> charDiff = computeCharLevelDiff("", newLine);
                    List<HighLight> newHL = extractHighlights(charDiff, HighLightType.ADD);

                    result.add(new HighLightedDiffLine(
                            DiffOp.ADD,
                            null,
                            newIdx + 1,
                            "",
                            newLine,
                            Collections.emptyList(),
                            newHL
                    ));
                    j = newIdx + 1;
                }
            }

            // 중간 끝난 뒤 남은 EQUAL
            while (i < endOld && j < endNew) {
                String line = oldVersion.get(i);
                result.add(new HighLightedDiffLine(
                        DiffOp.EQUAL,
                        i + 1,
                        j + 1,
                        line,
                        newVersion.get(j),
                        Collections.emptyList(),
                        Collections.emptyList()
                ));
                i++; j++;
            }
        }

        // 5) suffix EQUAL 처리
        for (int k = 0; k < suffix; k++, i++, j++) {
            int oldIdx = N - suffix + k;
            int newIdx = M - suffix + k;
            String line = oldVersion.get(oldIdx);
            result.add(new HighLightedDiffLine(
                    DiffOp.EQUAL,
                    oldIdx + 1,
                    newIdx + 1,
                    line,
                    newVersion.get(newIdx),
                    Collections.emptyList(),
                    Collections.emptyList()
            ));
        }

        return result;
    }

    private String normalize(String s) {
        return s.trim().replaceAll("\\s+", " ");
    }

    private List<DiffWord> computeCharLevelDiff(String lineA, String lineB) {
        if (lineA.isEmpty()) {
            return Arrays.stream(lineB.split(""))
                    .map(ch -> new DiffWord(DiffOp.ADD, ch))
                    .toList();
        }
        if (lineB.isEmpty()) {
            return Arrays.stream(lineA.split(""))
                    .map(ch -> new DiffWord(DiffOp.REMOVE, ch))
                    .toList();
        }
        List<String> aChars = lineA.codePoints()
                .mapToObj(cp -> new String(Character.toChars(cp)))
                .toList();
        List<String> bChars = lineB.codePoints()
                .mapToObj(cp -> new String(Character.toChars(cp)))
                .toList();
        return justComputeWordDiff(aChars, bChars);
    }

    private List<HighLight> extractHighlights(List<DiffWord> diffs,
                                              HighLightType type) {
        List<HighLight> hl = new ArrayList<>();
        int pos = 0;
        for (int i = 0; i < diffs.size(); i++) {
            DiffWord dw = diffs.get(i);
            String txt = dw.getText();
            int len = txt.length();
            if ((type == HighLightType.ADD && dw.getOp() == DiffOp.ADD) ||
                    (type == HighLightType.REMOVE && dw.getOp() == DiffOp.REMOVE)) {
                int start = pos;
                int length = len;
                int j = i + 1;
                while (j < diffs.size() && diffs.get(j).getOp() == dw.getOp()) {
                    length += diffs.get(j).getText().length();
                    j++;
                }
                hl.add(new HighLight(start, length, type));
                i = j - 1;
                pos += length;
            } else if (dw.getOp() == DiffOp.EQUAL) {
                pos += len;
            }
        }
        return hl;
    }

    private List<Edit> buildEditScript(List<String> a, List<String> b) {
        int N = a.size(), M = b.size(), max = N + M, offset = max, size = max * 2 + 1;
        int[] V = new int[size];
        List<int[]> traces = new ArrayList<>();
        int x = 0, y = 0;
        outer:
        for (int D = 0; D <= max; D++) {
            traces.add(V.clone());
            for (int k = -D; k <= D; k += 2) {
                int idx = k + offset;
                if (k == -D || (k != D && V[idx - 1] < V[idx + 1])) {
                    x = V[idx + 1];
                } else {
                    x = V[idx - 1] + 1;
                }
                y = x - k;
                while (x < N && y < M && a.get(x).equals(b.get(y))) {
                    x++; y++;
                }
                V[idx] = x;
                if (x >= N && y >= M) {
                    traces.add(V.clone());
                    break outer;
                }
            }
        }
        List<Edit> script = new ArrayList<>();
        x = N; y = M;
        for (int d = traces.size() - 1; d > 0; d--) {
            int[] Vprev = traces.get(d - 1);
            int k = x - y, idx = k + offset;
            int prevX, prevY;
            if (k == -d || (k != d && Vprev[idx - 1] < Vprev[idx + 1])) {
                prevX = Vprev[idx + 1];
                prevY = prevX - (k + 1);
                script.add(new Edit(Edit.Type.ADD, prevX, prevY));
            } else {
                prevX = Vprev[idx - 1] + 1;
                prevY = prevX - (k - 1);
                script.add(new Edit(Edit.Type.REMOVE, prevX - 1, prevY));
            }
            x = prevX; y = prevY;
        }
        Collections.reverse(script);
        return script;
    }

    private List<DiffWord> justComputeWordDiff(List<String> a, List<String> b) {
        int N = a.size(), M = b.size();
        int max = N + M;
        int offset = max;
        int size = 2 * max + 1;

        // V 배열 및 트레이스 저장소
        int[] V = new int[size];
        List<int[]> traces = new ArrayList<>();

        int x = 0, y = 0;
        outer:
        for (int D = 0; D <= max; D++) {
            traces.add(V.clone());
            for (int k = -D; k <= D; k += 2) {
                int idx = k + offset;
                // 이동 방향 결정
                if (k == -D || (k != D && V[idx - 1] < V[idx + 1])) {
                    x = V[idx + 1];
                } else {
                    x = V[idx - 1] + 1;
                }
                y = x - k;
                // Snake: 공통 부분 따라가기
                while (x < N && y < M && a.get(x).equals(b.get(y))) {
                    x++;
                    y++;
                }
                V[idx] = x;
                if (x >= N && y >= M) {
                    traces.add(V.clone());
                    break outer;
                }
            }
        }

        // 백트래킹: 최종 x=N, y=M부터 시작
        List<DiffWord> script = new ArrayList<>();
        x = N;
        y = M;
        for (int d = traces.size() - 1; d > 0; d--) {
            int[] Vprev = traces.get(d - 1);
            int k = x - y;
            int idx = k + offset;

            int prevX, prevY;
            // 내려왔는지(ADD) 오른쪽 왔는지(REMOVE) 판별
            if (k == -d || (k != d && Vprev[idx - 1] < Vprev[idx + 1])) {
                // ADD step
                prevX = Vprev[idx + 1];
                prevY = prevX - (k + 1);
                // 범위 검사 후에만 추가
                if (prevX >= 0 && prevX < M) {
                    script.add(new DiffWord(DiffOp.ADD, b.get(prevX)));
                }
            } else {
                // REMOVE step
                prevX = Vprev[idx - 1] + 1;
                prevY = prevX - (k - 1);
                int removeIdx = prevX - 1;
                // 범위 검사 후에만 추가
                if (removeIdx >= 0 && removeIdx < N) {
                    script.add(new DiffWord(DiffOp.REMOVE, a.get(removeIdx)));
                }
            }

            x = prevX;
            y = prevY;
        }

        Collections.reverse(script);
        return script;
    }

    private static class Edit {
        enum Type { REMOVE, ADD }
        final Type type;
        final int oldIndex;
        final int newIndex;
        Edit(Type type, int oldIndex, int newIndex) {
            this.type = type;
            this.oldIndex = oldIndex;
            this.newIndex = newIndex;
        }
    }
}