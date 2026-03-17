package umc.duckmelang.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.domain.chat.dto.ChatMessageRequestDto;
import umc.duckmelang.domain.chat.service.ChatService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatMessageHandler {

    private final ChatService chatService;

    /**
     * 클라이언트가 /pub/chat/{roomId} 로 메시지를 전송하면 호출됨
     * senderId는 STOMP CONNECT 시 검증된 JWT의 Principal에서 추출 (body에서 받지 않음)
     */
    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId,
                            ChatMessageRequestDto request,
                            Principal principal) {
        CustomUserDetails userDetails =
                (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        chatService.sendMessage(roomId, request.getContent(), userDetails.getMemberId());
    }
}
