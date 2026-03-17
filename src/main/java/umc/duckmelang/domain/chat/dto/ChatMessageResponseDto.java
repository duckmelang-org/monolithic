package umc.duckmelang.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;
import umc.duckmelang.domain.chat.domain.ChatMessage;

import java.time.ZonedDateTime;

@Getter
@Builder
public class ChatMessageResponseDto {

    private String messageId;
    private Long roomId;
    private Long senderId;
    private String content;
    private ZonedDateTime createdAt;

    public static ChatMessageResponseDto from(ChatMessage message) {
        return ChatMessageResponseDto.builder()
                .messageId(message.getId())
                .roomId(message.getRoomId())
                .senderId(message.getSenderId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
