package umc.duckmelang.global.concurrency;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedissonLock {
    String value(); // Lock의 이름 (고유값)
    long waitTime() default 5000L; // Lock 획득 시도 최대시간 ms
    long leaseTime() default 2000L; // Lock 점유 최대시간 ms
}
