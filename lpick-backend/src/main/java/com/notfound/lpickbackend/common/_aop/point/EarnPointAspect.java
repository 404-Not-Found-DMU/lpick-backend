package com.notfound.lpickbackend.common._aop.point;

import com.notfound.lpickbackend.common._event.point.PointAccrualRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Instant;

/**
 * EarnPoint 어노테이션의 동작 내역을 선언하기 위한 클래스
 * 사용법 :
 * 아래 어노테이션을 Service의 각 요청 메소드에 붙여 사용
 * @EarnPoint(
 *         activity = ActivityType.COMMENT_WRITE, // 본 어노테이션이 설정된 메소드의 커밋이 완전히 종료된 후 동작
 *         userId   = "#userDetail.oauthId", // 메서드 파라미터 이름 사용(컴파일 옵션 -parameters 필요)
 *         sourceId = "#result"        // #result == 본 어노테이션이 붙은 메소드의 리턴값 의미. 단, 모든 구현 사항이 리턴값을 가지지 못할 수 있다.
 *
 *     )
 * */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class EarnPointAspect {

    private final ApplicationEventPublisher publisher;
    private final ApplicationContext applicationContext; // ✅ 생성자 주입
    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();

    //
    @AfterReturning(pointcut = "@annotation(ann)", returning = "ret")
    public void publishAccrualEvent(JoinPoint jp, Object ret, EarnPoint ann) {
        try {
            log.info("어노테이션 호출 완료");
            
            Method method = ((MethodSignature) jp.getSignature()).getMethod();
            Object target = jp.getTarget();
            Object[] args = jp.getArgs();


            // ctx == SpEL이 참조할 변수/메서드/빈을 담는 컨테이너
            // 어노테이션을 통해 전달받은 변수
            MethodBasedEvaluationContext ctx =
                    new MethodBasedEvaluationContext(target, method, args, new DefaultParameterNameDiscoverer());


            //
            ctx.setBeanResolver(new BeanFactoryResolver(applicationContext));

            ctx.setVariable("result", ret);
            
            // 요청 사용자가 누구인지 확인하기위한 Authentication은 디폴트로 http 플로우 상의 SeurityContext를 참조해 사용한다.
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            // ctx에 사용자 설정
            if (auth != null) ctx.setVariable("auth", auth);

            String userId   = evalString(ann.userId(), ctx);
            String sourceId = evalString(ann.sourceId(), ctx);


            if (userId == null) {
                log.warn("EarnPoint SpEL eval returned null (userId={}, sourceId={}) at {}",
                        userId, sourceId, method);
                return; // 안전하게 스킵
            }

            // 이벤트 발행해 포인트 제공
            // sourceId를 비교하여, 포인트 중복 제공을 방지해야함.
            publisher.publishEvent(new PointAccrualRequestedEvent(
                    userId, ann.activity(), sourceId, Instant.now()
            ));

        } catch (Exception e) {
            log.error("Failed to publish PointAccrualRequestedEvent", e);
        }
    }

    private String evalString(String spel, EvaluationContext ctx) {
        if (spel == null || spel.isBlank()) return null;
        Object v = parser.parseExpression(spel).getValue(ctx);
        return v == null ? null : String.valueOf(v);
    }
}