package com.notfound.lpickbackend.common._wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IdResponse<T> {

    private String id;
    private T result;
}