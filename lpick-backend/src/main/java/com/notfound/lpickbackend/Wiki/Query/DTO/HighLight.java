package com.notfound.lpickbackend.Wiki.Query.DTO;

import com.notfound.lpickbackend.Wiki.Query.Enum.HighLightType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HighLight {
    private final int start;
    private final int length;
    private final HighLightType type;
}