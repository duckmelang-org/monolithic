package umc.duckmelang.domain.notification.service.notification;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.notification.domain.Notification;
import umc.duckmelang.domain.notification.domain.enums.NotificationType;

public interface NotificationCommandService {
    void sendLostData(String lastEventId, Long memberId, String emitterId, SseEmitter emitter);
    void send(Member sender, Member receiver, NotificationType notificationType, String content, String extraData);
    void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data);
    Notification patchNotificationRead(Long notificationId);
    void deleteNotification(Long notificationId);
}
