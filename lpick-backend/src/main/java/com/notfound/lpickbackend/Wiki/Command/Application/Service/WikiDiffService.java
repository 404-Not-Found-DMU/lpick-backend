package com.notfound.lpickbackend.Wiki.Command.Application.Service;

import com.notfound.lpickbackend.Wiki.Command.Application.DTO.DiffWord;
import com.notfound.lpickbackend.Wiki.Command.Application.DTO.InlineDiffLine;
import com.notfound.lpickbackend.Wiki.Enum.DiffOp;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WikiDiffService {

    /**
     * 주어진 두 버전의 줄 리스트(oldVersion, newVersion)를 받아
     * Myers 알고리즘으로 Edit Script를 만들고,
     * 이를 순회하며 InlineDiffLine 리스트로 변환합니다.
     */
    public List<InlineDiffLine> computeInlineDiff(List<String> oldVersion,
                                                  List<String> newVersion) {
        // 0) normalize
        List<String> a = oldVersion.stream().map(this::normalize).toList();
        List<String> b = newVersion.stream().map(this::normalize).toList();
        int N = a.size(), M = b.size();

        // 1) prefix 계산
        int prefix = 0;
        while (prefix < N && prefix < M && a.get(prefix).equals(b.get(prefix))) {
            prefix++;
        }
        // 2) suffix 계산
        int suffix = 0;
        while (suffix < N - prefix && suffix < M - prefix
                && a.get(N-1-suffix).equals(b.get(M-1-suffix))) {
            suffix++;
        }

        List<InlineDiffLine> result = new ArrayList<>();
        int i = 0, j = 0;

        // 3) prefix EQUAL
        for (; i < prefix && j < prefix; i++, j++) {
            result.add(new InlineDiffLine(
                    InlineDiffLine.Op.EQUAL,
                    i+1, j+1,
                    oldVersion.get(i), newVersion.get(j), null
            ));
        }

        // 4) middle diff (양쪽 영역 있을 때만)
        int endOld = N - suffix, endNew = M - suffix;
        if (prefix < endOld && prefix < endNew) {
            List<Edit> script = buildEditScript(
                    a.subList(prefix, endOld),
                    b.subList(prefix, endNew)
            );

            // **중복 필터를 위한 변수**
            Integer lastRemovedIdx = null;
            Integer lastAddedIdx   = null;

            for (Edit e : script) {
                int oldIdx = prefix + e.oldIndex;
                int newIdx = prefix + e.newIndex;

                // 4-1) 스크립트 전 EQUAL
                while (i < oldIdx && j < newIdx) {
                    result.add(new InlineDiffLine(
                            InlineDiffLine.Op.EQUAL,
                            i+1, j+1,
                            oldVersion.get(i), newVersion.get(j), null
                    ));
                    i++; j++;
                }

                // 4-2) REMOVE or ADD, 중복은 스킵
                if (e.type == Edit.Type.REMOVE) {
                    if (!Objects.equals(lastRemovedIdx, e.oldIndex)) {
                        result.add(new InlineDiffLine(
                                InlineDiffLine.Op.REPLACE,
                                oldIdx+1, null,
                                oldVersion.get(oldIdx), "",
                                computeWordLevelDiff(oldVersion.get(oldIdx), "")
                        ));
                        lastRemovedIdx = e.oldIndex;
                    }
                    // pointer 이동은 무조건
                    i = oldIdx + 1;
                } else {
                    if (!Objects.equals(lastAddedIdx, e.newIndex)) {
                        result.add(new InlineDiffLine(
                                InlineDiffLine.Op.REPLACE,
                                null, newIdx+1,
                                "", newVersion.get(newIdx),
                                computeWordLevelDiff("", newVersion.get(newIdx))
                        ));
                        lastAddedIdx = e.newIndex;
                    }
                    j = newIdx + 1;
                }
            }

            // 4-3) middle 끝난 뒤 남은 EQUAL
            while (i < endOld && j < endNew) {
                result.add(new InlineDiffLine(
                        InlineDiffLine.Op.EQUAL,
                        i+1, j+1,
                        oldVersion.get(i), newVersion.get(j), null
                ));
                i++; j++;
            }
        }

        // 5) suffix EQUAL
        for (int k = 0; k < suffix; k++, i++, j++) {
            int oldIdx = N - suffix + k;
            int newIdx = M - suffix + k;
            result.add(new InlineDiffLine(
                    InlineDiffLine.Op.EQUAL,
                    oldIdx+1, newIdx+1,
                    oldVersion.get(oldIdx),
                    newVersion.get(newIdx),
                    null
            ));
        }

        return result;
    }

    /**
     * Myers 알고리즘 구현: 두 시퀀스 a, b 에 대해 최소 편집(Edit) 스크립트를 생성
     * 반환된 Edit 리스트를 차례로 적용하면 a → b 로 변환됨
     */
    private List<Edit> buildEditScript(List<String> a, List<String> b) {
        int N = a.size(), M = b.size(), max = N + M;
        int offset = max, size = 2*max + 1;

        int[] V = new int[size];
        List<int[]> traces = new ArrayList<>();

        int x, y;
        int D;
        outer:
        for (D = 0; D <= max; D++) {
            traces.add(V.clone());
            for (int k = -D; k <= D; k += 2) {
                int idx = k + offset;
                // 이동 방향 결정: down or right
                if (k == -D || (k != D && V[idx-1] < V[idx+1])) {
                    x = V[idx+1];      // down: 추가
                } else {
                    x = V[idx-1] + 1;  // right: 삭제
                }
                y = x - k;
                // snake: 공통 부분 따라가기
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

        // 백트래킹: D 단계 역순으로 스크립트 추출
        List<Edit> script = new ArrayList<>();
        x = N; y = M;
        for (int d = traces.size() - 1; d > 0; d--) {
            int[] Vprev = traces.get(d-1);
            int k = x - y, idx = k + offset;
            int prevX, prevY;
            if (k == -d || (k != d && Vprev[idx-1] < Vprev[idx+1])) {
                // down → ADD
                prevX = Vprev[idx+1];
                prevY = prevX - (k+1);
                script.add(new Edit(Edit.Type.ADD, prevX, prevY));
            } else {
                // right → REMOVE
                prevX = Vprev[idx-1] + 1;
                prevY = prevX - (k-1);
                script.add(new Edit(Edit.Type.REMOVE, prevX-1, prevY));
            }
            x = prevX; y = prevY;
        }
        Collections.reverse(script);
        return script;
    }

    /** 공백 정규화: 앞뒤 trim + 연속 공백 하나로 축소 */
    private String normalize(String s) {
        return s.trim().replaceAll("\\s+", " ");
    }

    /**
     * 한 줄이 완전 추가/삭제된 경우: 단순 split
     * 그렇지 않으면 Myers 유사 알고리즘으로 word-level diff
     */
    public List<DiffWord> computeWordLevelDiff(String lineA, String lineB) {
        if (lineA.isBlank()) {
            return Arrays.stream(lineB.trim().split("\\s+"))
                    .filter(w -> !w.isEmpty())
                    .map(w -> new DiffWord(DiffOp.ADD, w))
                    .toList();
        }
        if (lineB.isBlank()) {
            return Arrays.stream(lineA.trim().split("\\s+"))
                    .filter(w -> !w.isEmpty())
                    .map(w -> new DiffWord(DiffOp.REMOVE, w))
                    .toList();
        }
        List<String> aWords = Arrays.stream(lineA.trim().split("\\s+"))
                .filter(w -> !w.isEmpty())
                .toList();
        List<String> bWords = Arrays.stream(lineB.trim().split("\\s+"))
                .filter(w -> !w.isEmpty())
                .toList();
        return justComputeWordDiff(aWords, bWords);
    }

    /** 단어 단위 Myers-style diff (기존 구현 유지) */
    public List<DiffWord> justComputeWordDiff(List<String> a, List<String> b) {
        int N = a.size(), M = b.size(), max = N + M;
        int offset = max, size = 2*max + 1;
        int[] V = new int[size];
        List<int[]> traces = new ArrayList<>();

        int x, y, D;
        outer:
        for (D = 0; D <= max; D++) {
            traces.add(V.clone());
            for (int k = -D; k <= D; k += 2) {
                int idx = k + offset;
                if (k == -D || (k != D && V[idx-1] < V[idx+1])) {
                    x = V[idx+1];
                } else {
                    x = V[idx-1] + 1;
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

        List<DiffWord> script = new ArrayList<>();
        x = N; y = M;
        for (int d = traces.size() - 1; d > 0; d--) {
            int[] Vprev = traces.get(d-1);
            int k = x - y, idx = k + offset;
            int prevX, prevY;
            if (k == -d || (k != d && Vprev[idx-1] < Vprev[idx+1])) {
                prevX = Vprev[idx+1];
                prevY = prevX - (k+1);
                script.add(new DiffWord(DiffOp.ADD, b.get(prevX)));
            } else {
                prevX = Vprev[idx-1] + 1;
                prevY = prevX - (k-1);
                script.add(new DiffWord(DiffOp.REMOVE, a.get(prevX-1)));
            }
            x = prevX; y = prevY;
        }
        Collections.reverse(script);
        return script;
    }

    // Edit.java (내부 DTO)
    private static class Edit {
        enum Type { REMOVE, ADD }
        final Type type;
        final int oldIndex, newIndex;
        Edit(Type type, int oldIndex, int newIndex) {
            this.type = type;
            this.oldIndex = oldIndex;
            this.newIndex = newIndex;
        }
    }
}