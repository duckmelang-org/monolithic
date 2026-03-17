package umc.duckmelang.domain.chat.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.chat.dto.ChatMessageResponseDto;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, Object> chatRedisTemplate;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final MessageListenerAdapter messageListenerAdapter;

    // 이미 구독 등록된 채널 추적 (중복 등록 방지)
    private final Set<String> subscribedChannels = ConcurrentHashMap.newKeySet();

    /**
     * Redis 채널에 메시지 발행
     * 채널이 처음 사용되면 구독도 함께 등록
     */
    public void publish(Long roomId, ChatMessageResponseDto message) {
        String channel = toChannel(roomId);
        subscribeIfAbsent(channel);
        chatRedisTemplate.convertAndSend(channel, message);
    }

    private void subscribeIfAbsent(String channel) {
        if (subscribedChannels.add(channel)) {
            redisMessageListenerContainer.addMessageListener(messageListenerAdapter, new ChannelTopic(channel));
        }
    }

    private String toChannel(Long roomId) {
        return "chat:" + roomId;
    }
}
