package umc.duckmelang.domain.chat.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.duckmelang.domain.application.domain.Application;
import umc.duckmelang.global.common.BaseEntity;

@Entity
@Table(name = "chat_room")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;
}
