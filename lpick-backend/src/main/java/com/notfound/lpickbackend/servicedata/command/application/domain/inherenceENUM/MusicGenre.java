package com.notfound.lpickbackend.servicedata.command.application.domain.inherenceENUM;

import lombok.Getter;

@Getter
public enum MusicGenre {
    CLASSICAL("클래식"),          // 클래식
    JAZZ("재즈"),               // 재즈
    POP("팝"),                // 팝
    BALLAD("발라드"),             // 발라드
    BLUES("블루스"),              // 블루스
    HIP_HOP("힙합"),            // 힙합
    COUNTRY_MUSIC("컨트리 뮤직"),      // 컨트리 뮤직
    FOLK_MUSIC("포크"),         // 포크 음악
    REGGAE("레게"),             // 레게
    DISCO("디스코"),              // 디스코
    ROCK_MUSIC("락"),         // 록 음악
    ELECTRONIC_MUSIC("전자 음악"),   // 전자 음악
    TROT("트로트"),               // 트로트
    ELECTRONICA("일렉트로닉"),        // 일렉트로닉 뮤직
    ROCK_AND_ROLL("로큰롤"),      // 로큰롤
    CONTEMPORARY_MUSIC("현대 음악"), // 현대음악
    OTHER("기타");

    private final String koLabel;

    MusicGenre(String koLabel) {
        this.koLabel = koLabel;
    }

    public String koLabel() {
        return koLabel;
    }

}
