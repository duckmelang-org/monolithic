package umc.duckmelang.domain.notificationsetting.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.global.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NotificationSetting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_setting_id", nullable = false)
    private Long id;

    @Setter
    @Column(nullable = false)
    private Boolean chatNotificationEnabled = true;  // 채팅 알림 수신 여부

    @Setter
    @Column(nullable = false)
    private Boolean requestNotificationEnabled = true;  // 동행 확정 요청 알림 수신 여부

    @Setter
    @Column(nullable = false)
    private Boolean reviewNotificationEnabled = true;  // 후기 작성 알림 수신 여부

    @Setter
    @Column(nullable = false)
    private Boolean bookmarkNotificationEnabled = true; //내 게시글 북마크 알림 수신 여부



}
