package com.notfound.lpickbackend.userInfo.command.domain;

/** UserAuth의 기입된 varchar 값을 검증하여 권한이 있는지 확인하기위한 ENUM.
 * ENUM으로 자체 관리 목적이 아니라, 단순히 검증 목적 String을 열거해둔 클래스입니다. */
public enum UserAuthentication {
    MEDIATOR, MANAGER, ADMIN
}
