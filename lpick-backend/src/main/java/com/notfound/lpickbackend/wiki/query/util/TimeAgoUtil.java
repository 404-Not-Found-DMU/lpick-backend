package com.notfound.lpickbackend.wiki.query.util;

import java.time.Duration;
import java.time.Instant;

public class TimeAgoUtil {

    /**
     * 과거 시각(createdAt)과 현재 시각(now)의 차이를 계산해서,
     * 60초 미만이면 “n초 전”,
     * 60분 미만이면 “n분 전”,
     * 24시간 미만이면 “n시간 전”,
     * 그 이상이면 “n일 전” 형식의 문자열을 반환합니다.
     *
     * @param createdAt 과거 시점 (예: 엔티티가 생성된 시간)
     * @param now       현재 시점 (보통 Instant.now()를 넘겨줌)
     * @return “n초 전”/“n분 전”/“n시간 전”/“n일 전” 형태의 문자열
     */
    public static String toTimeAgo(Instant createdAt, Instant now) {
        // 두 Instant 간의 차이를 Duration으로 계산
        Duration duration = Duration.between(createdAt, now);
        long seconds = duration.getSeconds();

        if (seconds < 60) {
            // 60초 미만 → “n초 전”
            return seconds + "초 전";
        }

        long minutes = seconds / 60;
        if (minutes < 60) {
            // 60분 미만 → “n분 전”
            return minutes + "분 전";
        }

        long hours = minutes / 60;
        if (hours < 24) {
            // 24시간 미만 → “n시간 전”
            return hours + "시간 전";
        }

        long days = hours / 24;
        return days + "일 전";
    }
}
