package com.notfound.lpickbackend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum SuccessCode {

    //SUCCESS(상태코드, "성공 문구");

    // 200
    SUCCESS(HttpStatus.OK, "OK"),
    // 201
    CREATE_SUCCESS(HttpStatus.CREATED, "Created"),
    WIKI_PAGE_CREATE_SUCCESS(HttpStatus.CREATED, "위키 문서 생성 성공"),

    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃 성공");

    private final HttpStatus httpStatus;
    private final String message;
}
