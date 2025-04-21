package umc.duckmelang.global.concurrency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockAspect {
//    private final RedissonClient redissonClient;

    @Around("@annotation(umc.duckmelang.global.concurrency.RedissonLock)")
    public void redissonLock(JoinPoint joinPoint) throws  Throwable {

    }
}
