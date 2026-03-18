package umc.duckmelang.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostViewCountService {

    private static final String VIEW_COUNT_KEY_PREFIX = "post:viewCount:";

    private final RedisTemplate<String, Long> viewCountRedisTemplate;

    public void increment(Long postId) {
        viewCountRedisTemplate.opsForValue().increment(VIEW_COUNT_KEY_PREFIX + postId);
    }

    public Long getViewCount(Long postId) {
        Long count = viewCountRedisTemplate.opsForValue().get(VIEW_COUNT_KEY_PREFIX + postId);
        return count != null ? count : 0L;
    }
}