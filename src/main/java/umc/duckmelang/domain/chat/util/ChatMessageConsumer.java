package umc.duckmelang.domain.chat.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.chat.domain.ChatMessage;
import umc.duckmelang.domain.chat.dto.ChatMessageEvent;
import umc.duckmelang.domain.chat.dto.ChatMessageResponseDto;
import umc.duckmelang.domain.chat.service.ChatService;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.global.config.RabbitMQConfig;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageConsumer {

    private final ChatService chatService;
    private final RedisPublisher redisPublisher;
    private final FcmService fcmService;

    @RabbitListener(queues = RabbitMQConfig.CHAT_QUEUE)
    public void consume(ChatMessageEvent event) {
        log.info("[RabbitMQ] 이벤트 수신 | roomId: {} | senderId: {}", event.getRoomId(), event.getSenderId());

        // MongoDB 저장
        ChatMessage message = chatService.saveMessage(event.getRoomId(), event.getSenderId(), event.getContent());
        ChatMessageResponseDto response = ChatMessageResponseDto.from(message);

        // 수신자, 발신자 조회
        Member receiver = chatService.getOpponent(event.getRoomId(), event.getSenderId());
        Member sender = chatService.getOpponent(event.getRoomId(), receiver.getId());

        // 항상 Redis pub/sub 브로드캐스트 (온라인 사용자에게 실시간 전달)
        redisPublisher.publish(event.getRoomId(), response);

        // 수신자 오프라인이면 FCM 푸시 알림 추가 발송
        if (!fcmService.isOnline(receiver.getId())) {
            fcmService.sendPushNotification(
                    receiver.getFcmToken(),
                    sender.getNickname(),
                    event.getContent(),
                    event.getRoomId()
            );
        }
    }
}
