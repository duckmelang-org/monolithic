package umc.duckmelang.domain.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.duckmelang.domain.application.domain.enums.ApplicationStatus;
import umc.duckmelang.domain.chatroom.domain.enums.ChatRoomStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomResponseDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatRoomItemListDto {
        private List<ChatRoomItemDto> chatRoomList;
        private Integer listSize;
        private Integer totalPage;
        private Long totalElements;
        private Boolean isFirst;
        private Boolean isLast;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatRoomItemDto{
        private Long chatRoomId;

        private Long oppositeId;
        private String oppositeNickname;
        private String oppositeProfileImage;

        private Long postId;
        private String postTitle;
        private String postImage;

        private String status;
        private String lastMessage;
        private LocalDateTime lastMessageTime;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatRoomDetailDto{
        private Long myId;
        private Long oppositeId;
        private String oppositeNickname;
        private String oppositeProfileImage;

        private Long postId;
        private String postTitle;
        private String postImage;

        //플래그
        private boolean isPostOwner;
        private ChatRoomStatus chatRoomStatus;
        private ApplicationStatus applicationStatus;
        private Long applicationId;
        private Long reviewId;
    }
}
