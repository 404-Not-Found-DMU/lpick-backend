package com.notfound.lpickbackend.Wiki.Command.Application.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class InlineDiffLine {
    public enum Op { EQUAL, REPLACE }

    private final Op op;
    private final Integer oldLineNumber;  // 원본 줄 번호(1-based), 제거된 줄에만 값
    private final Integer newLineNumber;  // 새 버전 줄 번호(1-based), 추가된 줄에만 값
    private final String oldLine;         // 원본 줄 텍스트 (EQUAL은 oldLine==newLine)
    private final String newLine;         // 새 버전 줄 텍스트
    private final List<DiffWord> tokens;  // REPLACE인 경우 단어 단위 diff, EQUAL인 경우 null
}
