package umc.duckmelang.domain.chatroom.service;

import org.springframework.data.domain.Page;
import umc.duckmelang.domain.chatroom.domain.ChatRoom;
import umc.duckmelang.domain.chatroom.dto.ChatRoomResponseDto;
import java.util.Optional;
public interface ChatRoomQueryService {
    public Page<ChatRoomResponseDto.ChatRoomItemDto> findAllChatRooms(Long memberId, int page);
    public Page<ChatRoomResponseDto.ChatRoomItemDto> findOngoingChatRooms(Long memberId, int page);
    public Page<ChatRoomResponseDto.ChatRoomItemDto> findConfirmedChatRooms(Long memberId, int page);
    public Page<ChatRoomResponseDto.ChatRoomItemDto> findTerminatedChatRooms(Long memberId, int page);
    public ChatRoomResponseDto.ChatRoomDetailDto findChatRoomDetail(Long memberId, Long chatRoomId);
    public Optional<ChatRoom> findChatRoom(Long chatRoomId);
    //채팅 횟수 조회
    Integer getChatRoomCount(Long postId);

}
