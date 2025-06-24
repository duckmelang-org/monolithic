package umc.duckmelang.domain.notification.service.notification;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import umc.duckmelang.domain.notification.domain.Notification;

import java.util.List;

public interface NotificationQueryService {
    List<Notification> getNotificationList(Long memberId);
    SseEmitter subscribe(Long memberId, String lastEventId);
    String makeTimeIncludeId(Long memberId);
    boolean hasLostData(String lastEventId);
    boolean existsById(Long notificationId);

}
