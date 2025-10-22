package com.notfound.lpickbackend.common.exception.stomp;

import com.notfound.lpickbackend.common.exception.ErrorCode;
import lombok.*;

/** STOMP에서 발생한  */
public class StompAppException extends RuntimeException {
    private final StompErrorCode errorCode;
    private final String details;


    /** 개발 시 사용  : StompErrorCode만 기입. 개발 상에서 발생한 에러 내역이 그대로 전달됨. */
    public StompAppException(StompErrorCode code) {
        this(code, code.getErrorMessage(), null);
    }

//    public StompAppException(StompErrorCode code, String message) {
//        this(code, message, null);
//    }

    public StompAppException(StompErrorCode code, String message, String details) {
        super(message);
        this.errorCode = code;
        this.details = details;
    }


    public StompErrorCode getErrorCode() { return errorCode; }
    public String getDetails() { return details; }
}