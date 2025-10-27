package com.notfound.lpickbackend.common.exception.stomp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StompErrorCode {
    // 서버내에러명(HTTP 에러스테이터스 기반, true, true);
    MESSAGE_SEND_MUST_NEED_LOGIN(HttpStatus.UNAUTHORIZED, "토론 내 의견 제안은 로그인 후 사용해주세요.", true, false);

    public final HttpStatus status;
    public final String errorMessage;
    public final boolean authenticationIssue; // 로그인 유무로 인한 문제인 경우
    public final boolean disconnectOnError; // 에러로 인해 웹소켓 연결을 해제해야하는가에 대한 유무(sofe deny로 사용할지, 말지 결정)
}
