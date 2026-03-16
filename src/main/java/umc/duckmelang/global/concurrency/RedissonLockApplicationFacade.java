package umc.duckmelang.global.concurrency;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.application.domain.Application;
import umc.duckmelang.domain.application.dto.request.ApplicationRequestDto;
import umc.duckmelang.domain.application.service.ApplicationService;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedissonLockApplicationFacade {

    private static final long WAIT_TIME_SECONDS = 10L;
    private static final long LEASE_TIME_SECONDS = 3L;

    private final RedissonClient redissonClient;
    private final ApplicationService applicationService;

    /**
     * Redisson RLock 방식 (Pub/Sub 기반 분산 락)
     *
     * - tryLock(waitTime, leaseTime, unit)
     *   - waitTime  : 락 획득을 최대 10초 대기 (초과 시 false 반환 → 예외)
     *   - leaseTime : 락 자동 만료 시간 3초 (데드락 방지)
     *
     * - Lettuce 스핀 락과 달리 Redis Pub/Sub 채널을 구독하여
     *   락 해제 이벤트를 받을 때까지 블로킹 → CPU 부하 낮음
     */
    public Application createApplicationWithRedisson(
            ApplicationRequestDto.CreateRequestDto request, Long memberId) throws InterruptedException {

        String lockKey = "redisson:lock:post:" + request.getPostId();
        RLock lock = redissonClient.getLock(lockKey);

        boolean acquired = lock.tryLock(WAIT_TIME_SECONDS, LEASE_TIME_SECONDS, TimeUnit.SECONDS);
        if (!acquired) {
            throw new RuntimeException("Redisson 락 획득 실패 (대기 시간 초과, key=" + lockKey + ")");
        }

        try {
            return applicationService.createApplication(request, memberId);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
