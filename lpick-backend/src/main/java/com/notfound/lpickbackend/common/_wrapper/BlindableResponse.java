package com.notfound.lpickbackend.common._wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
/** 반환 구조 래핑 목적의 제네릭 클래스. */
public class BlindableResponse<T> {
    /** 사용자 설정에 따라 블라인드 되어야하는지에 대한 여부를 표기하는 목적의 필드. 이 필드가 true라면 자식 클래스의 모든 필드 값이 null이어야함. boolean 기본값 false므로 블라인드시에만 명시적으로 빌더에 선언할 것. */
    private boolean isBlindedToOther;

    private T data;

    public static <T> BlindableResponse<T> of(boolean blinded, T data) {
        return new BlindableResponse<>(blinded, data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlindableResponse<?> that = (BlindableResponse<?>) o;
        return Objects.equals(isBlindedToOther, that.isBlindedToOther) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isBlindedToOther, data);
    }
}
