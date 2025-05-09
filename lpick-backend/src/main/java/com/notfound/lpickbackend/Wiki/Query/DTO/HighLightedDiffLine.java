package com.notfound.lpickbackend.Wiki.Query.DTO;

import com.notfound.lpickbackend.Wiki.Enum.DiffOp;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class HighLightedDiffLine {
    private final DiffOp op;
    private final Integer oldLineNumber;
    private final Integer newLineNumber;
    private final String oldText;
    private final String newText;
    private final List<HighLight> oldHighlights;
    private final List<HighLight> newHighlights;
}