package com.notfound.lpickbackend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 에러 이름(상태코드, 에러 문구)

    // 500 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에러 발생"),

    // 토큰 관련 에러
    NOT_VALID_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "유효하지 않은 refresh token입니다."),
    NOT_VALID_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "유효하지 않은 access token입니다."),
    SECURITY_CONTEXT_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Security Context에 인증 정보가 없습니다."),
    MISSING_AUTHORIZATION_HEADER(HttpStatus.BAD_REQUEST, "Authorization 헤더가 누락되었습니다."),

    // 400 에러
    NOT_MATCH_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "허용되지 않은 확장자입니다."),
    INVALID_FIELD_DATA(HttpStatus.BAD_REQUEST, "잘못된 필드 데이터입니다."),
    ACCESS_DENIED(HttpStatus.BAD_REQUEST, "올바르지 않은 접근입니다. "),
    EMPTY_TITLE(HttpStatus.BAD_REQUEST, "제목 값이 비어있습니다."),

    // 401 에러
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "인증 실패"),

    // 403 에러
    AUTHORIZATION_FAILED(HttpStatus.FORBIDDEN, "인가 실패"),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AccessToken이 쿠키에 존재하지 않습니다."),

    // 404 에러
    NOT_FOUND_REVISION(HttpStatus.NOT_FOUND, "버전 정보를 찾을 수 없습니다."),
    NOT_FOUND_WIKI(HttpStatus.NOT_FOUND, "위키 정보를 찾을 수 없습니다."),
    NOT_FOUND_USER_INFO(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."),
    NOT_FOUND_TIER(HttpStatus.NOT_FOUND, "티어 정보를 찾을 수 없습니다.")
    ;


    private final HttpStatus httpStatus;
    private final String message;
}
