package umc.duckmelang.domain.chat.util.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.chat.dto.event.ChatMessageEvent;
import umc.duckmelang.global.config.RabbitMQConfig;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(ChatMessageEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CHAT_EXCHANGE,
                RabbitMQConfig.CHAT_ROUTING_KEY,
                event
        );
        log.info("[RabbitMQ] 발행 완료 | roomId: {} | senderId: {}", event.getRoomId(), event.getSenderId());
    }
}