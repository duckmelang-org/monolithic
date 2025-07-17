package umc.duckmelang.domain.chatroom.converter;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.application.domain.Application;
import umc.duckmelang.domain.chatroom.domain.enums.ChatRoomStatus;
import umc.duckmelang.domain.chatroom.repository.ChatRoomRepository;
import umc.duckmelang.domain.review.domain.Review;
import umc.duckmelang.mongo.chatmessage.dto.ChatMessageRequestDto;
import umc.duckmelang.domain.chatroom.domain.ChatRoom;
import umc.duckmelang.domain.chatroom.dto.ChatRoomResponseDto;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.post.domain.Post;

import java.util.List;

@Component
public class ChatRoomConverter {
    public static ChatRoom toChatRoom(ChatMessageRequestDto.CreateChatMessageDto request, Post post, Member member) {
        return ChatRoom.builder()
                .post(post)
                .otherMember(member)
                .chatRoomStatus(ChatRoomStatus.ONGOING)
                .build();
    }

    public static ChatRoomResponseDto.ChatRoomItemListDto toChatRoomItemListDto(Page<ChatRoomResponseDto.ChatRoomItemDto> chatRooms) {
        List<ChatRoomResponseDto.ChatRoomItemDto> list = chatRooms.getContent();
        return ChatRoomResponseDto.ChatRoomItemListDto.builder()
                .chatRoomList(list)
                .isFirst(chatRooms.isFirst())
                .isLast(chatRooms.isLast())
                .listSize(list.size())
                .totalPage(chatRooms.getTotalPages())
                .totalElements(chatRooms.getTotalElements())
                .currentPage(chatRooms.getNumber())
                .build();
    }
}