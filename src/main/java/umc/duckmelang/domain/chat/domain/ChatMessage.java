package umc.duckmelang.domain.chat.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;

@Document(collection = "chat_messages")
@Getter
@Builder
public class ChatMessage {

    @Id
    private String id;

    @Indexed
    private Long roomId;

    private Long senderId;

    private String content;

    private ZonedDateTime createdAt;
}
