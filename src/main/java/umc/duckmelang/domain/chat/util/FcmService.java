package umc.duckmelang.domain.chat.util;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final SimpUserRegistry simpUserRegistry;

    public boolean isOnline(Long memberId) {
        return simpUserRegistry.getUser(String.valueOf(memberId)) != null;
    }

    public void sendPushNotification(String fcmToken, String senderNickname, String content, Long roomId) {
        if (fcmToken == null || fcmToken.isBlank()) {
            log.warn("[FCM] FCM 토큰 없음 | roomId: {}", roomId);
            return;
        }

        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(senderNickname)
                            .setBody(content)
                            .build())
                    .putData("roomId", String.valueOf(roomId))
                    .build();

            FirebaseMessaging.getInstance().send(message);
            log.info("[FCM] 푸시 알림 발송 완료 | roomId: {}", roomId);
        } catch (Exception e) {
            log.error("[FCM] 푸시 알림 발송 실패 | roomId: {} | error: {}", roomId, e.getMessage());
        }
    }
}
