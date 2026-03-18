package umc.duckmelang.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.chat.domain.ChatMessage;
import umc.duckmelang.domain.chat.domain.ChatRoom;
import umc.duckmelang.domain.chat.dto.ChatMessageResponseDto;
import umc.duckmelang.domain.chat.repository.ChatMessageRepository;
import umc.duckmelang.domain.chat.repository.ChatRoomRepository;
import umc.duckmelang.domain.chat.util.redis.RedisPublisher;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.ChatException;

import umc.duckmelang.domain.chat.dto.ChatRoomListResponseDto;

import org.hibernate.Hibernate;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RedisPublisher redisPublisher;

    // 메시지 전송
    @Transactional
    public ChatMessageResponseDto sendMessage(Long roomId, String content, Long senderId) {
        ChatRoom chatRoom = getChatRoom(roomId);
        validateParticipant(chatRoom, senderId);
        ChatMessage message = saveMessage(roomId, senderId, content);
        ChatMessageResponseDto response = ChatMessageResponseDto.from(message);
        broadcast(roomId, response);
        return response;
    }

    // 내 채팅방 목록 조회
    @Transactional(readOnly = true)
    public List<ChatRoomListResponseDto> getChatRooms(Long memberId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByMemberId(memberId);
        return chatRooms.stream()
                .map(room -> ChatRoomListResponseDto.of(room, memberId, getLatestMessage(room.getId())))
                .toList();
    }

    // 이전 메시지 조회
    @Transactional(readOnly = true)
    public Slice<ChatMessageResponseDto> getMessages(Long roomId, int page, int size) {
        getChatRoom(roomId);
        return chatMessageRepository
                .findByRoomIdOrderByCreatedAtDesc(roomId, PageRequest.of(page, size))
                .map(ChatMessageResponseDto::from);
    }

    // 채팅방 찾기 + 참여자 검증 (Handler에서 단일 트랜잭션으로 처리)
    @Transactional(readOnly = true)
    public void validateChatRoomParticipant(Long roomId, Long senderId) {
        ChatRoom chatRoom = getChatRoom(roomId);
        validateParticipant(chatRoom, senderId);
    }

    // 채팅방 찾기
    @Transactional(readOnly = true)
    public ChatRoom getChatRoom(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ErrorStatus.CHAT_ROOM_NOT_FOUND));
    }

    // 메시지 저장
    public ChatMessage saveMessage(Long roomId, Long senderId, String content) {
        return chatMessageRepository.save(ChatMessage.builder()
                .roomId(roomId)
                .senderId(senderId)
                .content(content)
                .createdAt(ZonedDateTime.now())
                .build());
    }

    private void broadcast(Long roomId, ChatMessageResponseDto response) {
        redisPublisher.publish(roomId, response);
    }

    // 상대방 Member 조회 (senderId 기준으로 반대편 유저 반환)
    @Transactional(readOnly = true)
    public umc.duckmelang.domain.member.domain.Member getOpponent(Long roomId, Long myMemberId) {
        ChatRoom chatRoom = getChatRoom(roomId);
        Long applicantId = chatRoom.getApplication().getMember().getId();
        umc.duckmelang.domain.member.domain.Member opponent = myMemberId.equals(applicantId)
                ? chatRoom.getApplication().getPost().getMember()
                : chatRoom.getApplication().getMember();
        Hibernate.initialize(opponent); // 트랜잭션 내에서 프록시 강제 초기화
        return opponent;
    }

    // 마지막 메시지 조회
    private ChatMessage getLatestMessage(Long roomId) {
        return chatMessageRepository.findTopByRoomIdOrderByCreatedAtDesc(roomId).orElse(null);
    }

    // 참여자 검증
    @Transactional(readOnly = true)
    public void validateParticipant(ChatRoom chatRoom, Long senderId) {
        Long applicantId = chatRoom.getApplication().getMember().getId();
        Long hostId = chatRoom.getApplication().getPost().getMember().getId();
        if (!senderId.equals(applicantId) && !senderId.equals(hostId)) {
            throw new ChatException(ErrorStatus.CHAT_NOT_PARTICIPANT);
        }
    }
}
