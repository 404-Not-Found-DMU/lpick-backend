package com.notfound.lpickbackend.common._aop.point;

import com.notfound.lpickbackend.common._event.point.ActivityType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EarnPoint {
    ActivityType activity();
    String userId();   // 예: "#cmd.authorId" or "#result.authorId"
    String sourceId(); // 예: "#cmd.commentId" or "#result.id"
}