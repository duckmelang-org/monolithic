package umc.duckmelang.domain.chat.util.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.chat.dto.ChatMessageResponseDto;

@Slf4j
@Service
public class RedisSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public RedisSubscriber(SimpMessagingTemplate messagingTemplate,
                           @Qualifier("redisObjectMapper") ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Redis 채널에 메시지가 발행되면 호출됨
     * JSON 역직렬화 후 STOMP 구독자에게 전달
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            ChatMessageResponseDto response = objectMapper.readValue(message.getBody(), ChatMessageResponseDto.class);
            log.info("[Redis] 수신 완료 | roomId: {} | senderId: {}", response.getRoomId(), response.getSenderId());
            messagingTemplate.convertAndSend("/sub/chat/" + response.getRoomId(), response);
        } catch (Exception e) {
            log.error("[Redis] 메시지 처리 실패: {}", e.getMessage());
        }
    }
}
