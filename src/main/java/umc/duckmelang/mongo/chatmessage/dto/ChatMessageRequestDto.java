package umc.duckmelang.mongo.chatmessage.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import umc.duckmelang.mongo.chatmessage.domain.enums.MessageType;

import java.util.List;

public class ChatMessageRequestDto {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateChatMessageDto {
        private Long chatRoomId;
        @NotNull
        private Long senderId;
        @NotNull
        private Long receiverId;
        @NotNull
        private Long postId;
        @NotNull
        private MessageType messageType;

        private String text; // 링크URL 또는 텍스트
        @Size(max = 5, message = "이미지는 최대 5장까지 첨부할 수 있습니다.")
        private List<MultipartFile> imageFiles; // 여러 개의 이미지 파일
        private MultipartFile file; // 하나의 일반 파일
    }
}
