package umc.duckmelang.domain.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import umc.duckmelang.domain.chat.domain.ChatMessage;

import java.util.Optional;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    // roomId 기준 최신순 페이징 (Slice = 다음 페이지 존재 여부만 확인, count 쿼리 없음)
    Slice<ChatMessage> findByRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);

    // 채팅방의 가장 최근 메시지 1개
    Optional<ChatMessage> findTopByRoomIdOrderByCreatedAtDesc(Long roomId);
}
