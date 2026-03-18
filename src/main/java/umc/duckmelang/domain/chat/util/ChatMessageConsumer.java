package umc.duckmelang.domain.chat.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.chat.domain.ChatMessage;
import umc.duckmelang.domain.chat.dto.ChatMessageEvent;
import umc.duckmelang.domain.chat.dto.ChatMessageResponseDto;
import umc.duckmelang.domain.chat.service.ChatService;
import umc.duckmelang.global.config.RabbitMQConfig;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageConsumer {

    private final ChatService chatService;
    private final RedisPublisher redisPublisher;

    @RabbitListener(queues = RabbitMQConfig.CHAT_QUEUE)
    public void consume(ChatMessageEvent event) {
        log.info("[RabbitMQ] 이벤트 수신 | roomId: {} | senderId: {}", event.getRoomId(), event.getSenderId());

        // MongoDB 저장
        ChatMessage message = chatService.saveMessage(event.getRoomId(), event.getSenderId(), event.getContent());

        // Redis pub/sub 브로드캐스트
        ChatMessageResponseDto response = ChatMessageResponseDto.from(message);
        redisPublisher.publish(event.getRoomId(), response);
    }
}
