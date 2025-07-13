package com.notfound.lpickbackend.common._super;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
/** 구조 공유 목적의 클래스. 해당 클래스만 구현/반환 하지 않게 유의. */
public abstract class BlindableResponse {
    /** 사용자 설정에 따라 블라인드 되어야하는지에 대한 여부를 표기하는 목적의 필드. 이 필드가 true라면 자식 클래스의 모든 필드 값이 null이어야함. boolean 기본값 false므로 블라인드시에만 명시적으로 빌더에 선언할 것. */
    private boolean isBlinded;
}
