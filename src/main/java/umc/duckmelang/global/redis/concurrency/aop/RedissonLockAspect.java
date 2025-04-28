package umc.duckmelang.global.redis.concurrency.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.exception.LockAcquisitionException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import umc.duckmelang.global.redis.concurrency.CustomSpringELParser;
import umc.duckmelang.global.redis.concurrency.RedissonLock;

import java.lang.reflect.Method;
import java.sql.SQLException;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockAspect {
    final RedissonClient redissonClient;
    final TransactionProceedAspect transactionProceedAspect;
    protected static final String REDISSON_LOCK_PREFIX = "lock:";

    @Around("@annotation(umc.duckmelang.global.redis.concurrency.RedissonLock)")
    public Object redissonLock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 인터페이스가 아닌 구현 클래스의 메소드에서 어노테이션 가져오기
        Method implementationMethod = joinPoint.getTarget().getClass()
                .getMethod(method.getName(), method.getParameterTypes());
        RedissonLock redissonLock = implementationMethod.getAnnotation(RedissonLock.class);
        log.info("redissonLock {} {} {}",signature.getParameterNames(), joinPoint.getArgs(), redissonLock.key());
        String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), redissonLock.key());
        RLock rLock = redissonClient.getLock(key);

        boolean lockAcquired = false;
        try {
            lockAcquired = rLock.tryLock(redissonLock.waitTime(), redissonLock.leaseTime(), redissonLock.timeUnit());
            if (!lockAcquired) {
                throw new LockAcquisitionException("Failed to acquire lock: " + key, new SQLException()); // 명시적 예외 던지기
            }
            log.info("redisson key {}", key);
            return transactionProceedAspect.proceed(joinPoint);
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            if (lockAcquired) {
                try {
                    rLock.unlock();
                    log.info("Lock released for key: {}", key);
                } catch (IllegalMonitorStateException e) {
                    log.info("Redisson Lock Already Unlocked {} {}",
                            method.getName(), key);
                }
            }
        }
    }
}
