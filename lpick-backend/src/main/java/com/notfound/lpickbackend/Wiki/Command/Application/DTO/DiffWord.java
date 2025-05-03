package com.notfound.lpickbackend.Wiki.Command.Application.DTO;

import com.notfound.lpickbackend.Wiki.Enum.DiffOp;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiffWord {
    private final DiffOp op;
    private final String text;
}
