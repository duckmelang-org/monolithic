package umc.duckmelang.domain.post.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.post.repository.PostRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostViewCountSyncScheduler {

    private static final String VIEW_COUNT_KEY_PREFIX = "post:viewCount:";
    private static final String VIEW_COUNT_KEY_PATTERN = VIEW_COUNT_KEY_PREFIX + "*";

    private final RedisTemplate<String, Long> viewCountRedisTemplate;
    private final PostRepository postRepository;

    @Scheduled(fixedRate = 300000) // 5분마다 실행
    @CacheEvict(value = "popularPosts", allEntries = true)
    @Transactional
    public void syncViewCountsToDB() {
        log.info("[ViewCount Sync] Redis → DB 동기화 시작");

        ScanOptions scanOptions = ScanOptions.scanOptions().match(VIEW_COUNT_KEY_PATTERN).count(100).build();

        try (Cursor<String> cursor = viewCountRedisTemplate.scan(scanOptions)) {
            while (cursor.hasNext()) {
                String key = cursor.next();
                Long redisCount = viewCountRedisTemplate.opsForValue().get(key);

                if (redisCount == null || redisCount == 0) {
                    continue;
                }

                Long postId = extractPostId(key);
                postRepository.findById(postId).ifPresent(post -> {
                    post.addViewCount(redisCount);
                    log.debug("[ViewCount Sync] postId={}, +{}", postId, redisCount);
                });

                viewCountRedisTemplate.delete(key);
            }
        }

        log.info("[ViewCount Sync] 완료");
    }

    private Long extractPostId(String key) {
        return Long.parseLong(key.replace(VIEW_COUNT_KEY_PREFIX, ""));
    }
}
