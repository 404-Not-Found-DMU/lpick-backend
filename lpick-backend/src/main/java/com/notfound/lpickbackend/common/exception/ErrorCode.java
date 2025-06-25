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
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AccessToken이 쿠키에 존재하지 않습니다."),

    // 서비스 내 사용자 권한 관련 에러
    INSUFFICIENT_ACCESS_LEVEL(HttpStatus.FORBIDDEN, "접근 권한이 부족합니다."),

    // 400 에러
    NOT_MATCH_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "허용되지 않은 확장자입니다."),
    INVALID_FIELD_DATA(HttpStatus.BAD_REQUEST, "잘못된 필드 데이터입니다."),
    ACCESS_DENIED(HttpStatus.BAD_REQUEST, "올바르지 않은 접근입니다. "),
    EMPTY_TITLE(HttpStatus.BAD_REQUEST, "제목 값이 비어있습니다."),
    ALREADY_HAS_REVIEW_IN_WIKIPAGE(HttpStatus.BAD_REQUEST, "이미 리뷰를 작성했습니다."),
    ALREADY_HAS_BOOKMARK(HttpStatus.BAD_REQUEST, "이미 추가되어있는 북마크입니다."),
    INVALID_PAGE_REQUEST(HttpStatus.BAD_REQUEST,"page 옵션이 올바르지 않습니다. 음수 등을 작성할 수 없습니다." ),

    // 401 에러
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "인증 실패"),

    // 403 에러
    AUTHORIZATION_FAILED(HttpStatus.FORBIDDEN, "인가 실패"),
    // 403에러인데 다양한 리소스에서 같이 사용해도 괜찮을것같습니다.
    FORBIDDEN_RESOURCE_ACCESS(HttpStatus.FORBIDDEN, "해당 리소스에 접근 권한이 없습니다."),

    // 404 에러
    NOT_FOUND_REVISION(HttpStatus.NOT_FOUND, "버전 정보를 찾을 수 없습니다."),
    NOT_FOUND_WIKI(HttpStatus.NOT_FOUND, "위키 정보를 찾을 수 없습니다."),

    NOT_FOUND_WIKI_BOOKMARK(HttpStatus.NOT_FOUND, "북마크 정보를 찾을 수 없습니다."),
    NOT_FOUND_REVIEW(HttpStatus.NOT_FOUND, "리뷰 정보를 찾을 수 없습니다."),
    NOT_FOUND_ARTICLE(HttpStatus.NOT_FOUND, "존재하지 않거나 삭제된 게시글입니다."),

    NOT_FOUND_USER_INFO(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."),
    NOT_FOUND_TIER(HttpStatus.NOT_FOUND, "티어 정보를 찾을 수 없습니다."),
    NOT_FOUND_BOOKMARK(HttpStatus.NOT_FOUND, "북마크 정보를 찾을 수 없습니다."),

    // 처리 방법에 논의가 필요한 에러 코드 임시할당용
    DO_NOT_KEEP_UP_THIS_ERROR_WHEN_MERGE(HttpStatus.I_AM_A_TEAPOT, "이 에러 코드는 실제 사용 목적이 아닙니다."),
    ;


    private final HttpStatus httpStatus;
    private final String message;
}
