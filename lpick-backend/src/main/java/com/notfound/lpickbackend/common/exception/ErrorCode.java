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

    // 서비스 내 사용자 권한 관련 에러
    INSUFFICIENT_ACCESS_LEVEL(HttpStatus.FORBIDDEN, "접근 권한이 부족합니다."),

    // 400 에러
    NOT_MATCH_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "허용되지 않은 확장자입니다."),
    INVALID_FIELD_DATA(HttpStatus.BAD_REQUEST, "잘못된 필드 데이터입니다."),
    ACCESS_DENIED(HttpStatus.BAD_REQUEST, "올바르지 않은 접근입니다. "),
    EMPTY_TITLE(HttpStatus.BAD_REQUEST, "제목 값이 비어있습니다."),

    // 401 에러
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "인증 실패"),

    // 403 에러
    AUTHORIZATION_FAILED(HttpStatus.FORBIDDEN, "인가 실패"),

    // 404 에러
    NOT_FOUND_REVISION(HttpStatus.NOT_FOUND, "버전 정보를 찾을 수 없습니다."),
    NOT_FOUND_WIKI(HttpStatus.NOT_FOUND, "위키 정보를 찾을 수 없습니다."),
    NOT_FOUND_WIKIBOOKMARK(HttpStatus.NOT_FOUND, "북마크 정보를 찾을 수 없습니다."),

    // 처리 방법에 논의가 필요한 에러 코드
    DO_NOT_KEEP_UP_THIS_ERROR_WHEN_MERGE(HttpStatus.I_AM_A_TEAPOT, "이 에러 코드는 실제 사용 목적이 아닙니다.")

    ;


    private final HttpStatus httpStatus;
    private final String message;
}
