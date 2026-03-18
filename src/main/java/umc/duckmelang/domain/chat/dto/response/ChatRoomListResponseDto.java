package umc.duckmelang.domain.chat.dto.response;

import lombok.Builder;
import lombok.Getter;
import umc.duckmelang.domain.chat.domain.ChatMessage;
import umc.duckmelang.domain.chat.domain.ChatRoom;
import umc.duckmelang.domain.member.domain.Member;

import java.time.ZonedDateTime;

@Getter
@Builder
public class ChatRoomListResponseDto {

    private Long roomId;
    private String opponentNickname;
    private String lastMessage;
    private ZonedDateTime lastMessageAt;

    public static ChatRoomListResponseDto of(ChatRoom chatRoom, Long myMemberId, ChatMessage lastChatMessage) {
        Long applicantId = chatRoom.getApplication().getMember().getId();
        Member opponent = applicantId.equals(myMemberId)
                ? chatRoom.getApplication().getPost().getMember()
                : chatRoom.getApplication().getMember();

        return ChatRoomListResponseDto.builder()
                .roomId(chatRoom.getId())
                .opponentNickname(opponent.getNickname())
                .lastMessage(lastChatMessage != null ? lastChatMessage.getContent() : null)
                .lastMessageAt(lastChatMessage != null ? lastChatMessage.getCreatedAt() : null)
                .build();
    }
}
