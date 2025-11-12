package com.notfound.lpickbackend.common._event.point.handler;

import com.notfound.lpickbackend.common._event.point.PointAccrualRequestedEvent;
import com.notfound.lpickbackend.common._event.point.service.PointAccrualService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointAccrualHandler {
    private final PointAccrualService pointAccrualService;

    // 이벤트 발행되었던 """트랜잭션이 커밋된 후에 동작 보증(AFTER_COMMIT)"""
    // 이벤트 발행으로 'PointAccrualRequestedEvent'가 나타나면 동작
    // fallbackeExecution == 트랜잭션이 없으면 즉시 실행. 단순 테스트 용 임시 옵션으로 실제 사용시 false 변경 후 사용합니다.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(PointAccrualRequestedEvent e) {
        log.info("이벤트 발행은 됐네요");
        pointAccrualService.accrue(e.userId(), e.activity(), e.sourceId()
        );
    }
}