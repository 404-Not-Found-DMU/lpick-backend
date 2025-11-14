package com.notfound.lpickbackend.common.util;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Specs {

    private Specs() {}

    /**
     * AND 조합.
     * 전달된 스펙 중 null을 제거하고 모두 AND로 묶는다.
     * 아무 조건이 없으면 "참" (conjunction)을 반환 → 전체 조회 + 페이징/정렬만 적용.
     */
    @SafeVarargs
    public static <T> Specification<T> all(Specification<T>... specs) {
        List<Specification<T>> list = Stream.of(specs)
                .filter(Objects::nonNull)
                .toList();

        if (list.isEmpty()) {
            // WHERE 1=1
            return (root, q, cb) -> cb.conjunction();
        }
        return Specification.allOf(list);
    }

    /**
     * OR 조합.
     * 전달된 스펙 중 null을 제거하고 모두 OR로 묶는다.
     * 아무 조건이 없으면 "거짓" (disjunction)을 반환 → 사용처에서 의미 있게만 호출할 것.
     */
    @SafeVarargs
    public static <T> Specification<T> any(Specification<T>... specs) {
        List<Specification<T>> list = Stream.of(specs)
                .filter(Objects::nonNull)
                .toList();

        if (list.isEmpty()) {
            // WHERE 1=0
            return (root, q, cb) -> cb.disjunction();
        }
        return Specification.anyOf(list);
    }

}
