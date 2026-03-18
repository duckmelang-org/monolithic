package umc.duckmelang.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.domain.chat.dto.event.ChatMessageEvent;
import umc.duckmelang.domain.chat.dto.request.ChatMessageRequestDto;
import umc.duckmelang.domain.chat.service.ChatService;
import umc.duckmelang.domain.chat.util.rabbitmq.ChatMessagePublisher;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatMessageHandler {

    private final ChatService chatService;
    private final ChatMessagePublisher chatMessagePublisher;

    /**
     * 클라이언트가 /pub/chat/{roomId} 로 메시지를 전송하면 호출됨
     * senderId는 STOMP CONNECT 시 검증된 JWT의 Principal에서 추출 (body에서 받지 않음)
     * 참여자 검증 후 RabbitMQ Queue에 이벤트 발행 → 즉시 응답
     * 나머지 처리(MongoDB 저장, 브로드캐스트)는 Consumer가 비동기로 처리
     */
    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId,
                            ChatMessageRequestDto request,
                            Principal principal) {
        CustomUserDetails userDetails =
                (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Long senderId = userDetails.getMemberId();

        chatService.validateChatRoomParticipant(roomId, senderId);

        chatMessagePublisher.publish(ChatMessageEvent.builder()
                .roomId(roomId)
                .senderId(senderId)
                .content(request.getContent())
                .build());
    }
}
