package umc.duckmelang.global.redis.concurrency;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedissonLock {
    String key(); // Lock의 이름 (고유값)
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS; // 락 시간 단위
    long waitTime() default 100L; // Lock 획득 시도 최대시간 ms
    long leaseTime() default 3000L; // Lock 점유 최대시간 ms
}
