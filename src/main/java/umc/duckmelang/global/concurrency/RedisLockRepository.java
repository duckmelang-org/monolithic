package umc.duckmelang.global.concurrency;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisLockRepository {

    private final RedisTemplate<String, String> redisTemplate;

    // 락 획득
    public Boolean lock(Long key){
        return redisTemplate.opsForValue()
                .setIfAbsent(generateKey(key), "lock", Duration.ofMillis(3_000));
    }

    // 락 해제
    public Boolean unlock(Long key){
        return redisTemplate.delete(generateKey(key));
    }

    private String generateKey(Long key){
        return "lock:post:" + key;
    }
}
