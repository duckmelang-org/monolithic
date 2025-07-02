package umc.duckmelang.domain.report.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.duckmelang.domain.chatroom.domain.ChatRoom;
import umc.duckmelang.domain.report.domain.enums.ReportType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DiscriminatorValue(ReportType.Values.CHAT)
public class ChatReport extends Report{
    @OneToOne
    private ChatRoom chatRoom;
}
