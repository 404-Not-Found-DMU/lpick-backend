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
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃 성공"),
    REFRESH_SUCCESS(HttpStatus.OK, "Refresh 요청 성공"),
    DEV_TOKEN_CREATE_SUCCESS(HttpStatus.OK, "개발자 전용 토큰 생성 성공"),

    // 201
    CREATE_SUCCESS(HttpStatus.CREATED, "Created"),
    WIKI_PAGE_CREATE_SUCCESS(HttpStatus.CREATED, "위키 문서 생성 성공"),

    // PAGE_REVISION_REVERT_SUCCESS(HttpStatus.OK, "대상 버전으로 되돌리기 성공");

    // 204
    NO_CONTENT(HttpStatus.NO_CONTENT, "요청 처리 완료. 반환 내역 없음.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
