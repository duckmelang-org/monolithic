package umc.duckmelang.mongo.chatmessage.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import umc.duckmelang.mongo.chatmessage.domain.ChatMessage;
import umc.duckmelang.mongo.chatmessage.dto.ChatMessageRequestDto;
import umc.duckmelang.mongo.chatmessage.dto.ChatMessageResponseDto;
import umc.duckmelang.domain.chatroom.domain.ChatRoom;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.ChatMessageException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChatMessageConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 채팅 메세지 객체 생성
    public static ChatMessage toChatMessage(ChatMessageRequestDto.@Valid CreateChatMessageDto request, ChatRoom chatRoom) {
        return ChatMessage.builder()
                .chatRoomId(chatRoom.getId())
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .messageType(request.getMessageType())
                .text(request.getText())
                .uuids(List.of())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static ChatMessage toChatMessageWithImages(ChatMessageRequestDto.@Valid CreateChatMessageDto request, ChatRoom chatRoom, List<String> imageUrls, List<String> uuids) {
        return ChatMessage.builder()
                .chatRoomId(chatRoom.getId())
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .messageType(request.getMessageType())
                .imageUrls(imageUrls)
                .uuids(uuids)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static ChatMessage toChatMessageWithFile(ChatMessageRequestDto.@Valid CreateChatMessageDto request, ChatRoom chatRoom, String fileUrl, String uuid) {
        return ChatMessage.builder()
                .chatRoomId(chatRoom.getId())
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .messageType(request.getMessageType())
                .fileUrl(fileUrl)
                .uuids(List.of(uuid))
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 채팅 메세지 DTO 생성
    public static ChatMessageResponseDto.ChatMessageDto toChatMessageDto(ChatMessage chatMessage) {
        ChatMessageResponseDto.ChatMessageDto.ChatMessageDtoBuilder builder = ChatMessageResponseDto.ChatMessageDto.builder()
                .id(chatMessage.getId())
                .chatRoomId(chatMessage.getChatRoomId())
                .senderId(chatMessage.getSenderId())
                .receiverId(chatMessage.getReceiverId())
                .messageType(chatMessage.getMessageType())
                .createdAt(chatMessage.getCreatedAt());

        // 메시지 타입에 따라 필드 설정
        switch (chatMessage.getMessageType()) {
            case TEXT:
                builder.text(chatMessage.getText());
                break;
            case IMAGE:
                builder.imageUrls(chatMessage.getImageUrls());
                break;
            case FILE:
                builder.fileUrl(chatMessage.getFileUrl());
                break;
            case LINK:
                builder.text(chatMessage.getText()); // Link도 text 사용
                break;
            default:
                throw new ChatMessageException(ErrorStatus.INVALID_MESSAGE_TYPE);
        }

        return builder.build();
    }

    // 통신 응답 메세지 생성
    public static TextMessage toTextMessage(ChatMessage chatMessage) {
        try {
            // DTO 생성
            ChatMessageResponseDto.ChatMessageDto response = toChatMessageDto(chatMessage);

            // JSON 변환 및 TextMessage 생성
            String responseJson = objectMapper.writeValueAsString(response);
            return new TextMessage(responseJson);

            // 에러케이스에 맞는 커스텀 예외처리 진행
        } catch (JsonMappingException e) {
            throw new ChatMessageException(ErrorStatus.INVALID_JSON_FORMAT);
        } catch (JsonProcessingException e) {
            throw new ChatMessageException(ErrorStatus.JSON_CONVERSION_FAILED);
        } catch (Exception e) {
            throw new ChatMessageException(ErrorStatus.JSON_PROCESSING_ERROR);
        }
    }

    // 특정 채팅방 내 채팅 메시지 조회 결과 DTO 생성
    public static ChatMessageResponseDto.ChatMessageListDto toChatMessageListDto(Slice<ChatMessage> chatMessageSlice) {
        // Slice의 콘텐츠(ChatMessage)를 ChatMessageDto로 변환
        List<ChatMessageResponseDto.ChatMessageDto> chatMessageDtoList = chatMessageSlice.getContent().stream()
                .map(ChatMessageConverter::toChatMessageDto)
                .collect(Collectors.toList());

        // hasNext는 Slice에서 제공하는 값을 그대로 사용
        boolean hasNext = chatMessageSlice.hasNext();

        // 마지막 메시지의 ID 추출 (리스트가 비어 있지 않은 경우)
        String lastMessageId = !chatMessageDtoList.isEmpty()
                ? chatMessageDtoList.get(chatMessageDtoList.size() - 1).getId()
                : null;

        // 최종 결과 DTO 생성 및 반환
        return ChatMessageResponseDto.ChatMessageListDto.builder()
                .chatMessageList(chatMessageDtoList)
                .hasNext(hasNext)
                .lastMessageId(lastMessageId)
                .build();
    }
}
