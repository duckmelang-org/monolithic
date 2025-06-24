package umc.duckmelang.domain.notification.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.notification.converter.NotificationConverter;
import umc.duckmelang.domain.notification.domain.Notification;
import umc.duckmelang.domain.notification.domain.enums.NotificationType;
import umc.duckmelang.domain.notification.dto.NotificationResponseDto;
import umc.duckmelang.domain.notification.repository.EmitterRepository;
import umc.duckmelang.domain.notification.repository.NotificationRepository;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.NotificationException;


@Service
@RequiredArgsConstructor
@Transactional
public class NotificationCommandServiceImpl implements NotificationCommandService {

    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;

    private static final Logger logger = LoggerFactory.getLogger(NotificationCommandServiceImpl.class);

    @Override
    public void sendLostData(String lastEventId, Long memberId, String emitterId, SseEmitter emitter) { // (6)
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByMemberId(memberId);
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }
    @Override
    public void send(Member sender, Member receiver, NotificationType notificationType, String content, String extraData ) {
        Notification notification = notificationRepository.save(NotificationConverter.toNotification(sender, receiver, notificationType, content, extraData)); // (2-1)
        Long receiverId = receiver.getId(); // (2-2)
        String eventId = receiverId + "_" + System.currentTimeMillis(); // (2-3)
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(receiverId);

        emitters.forEach((key, emitter) -> {
            // NotificationDto 생성
            NotificationResponseDto.NotificationDto notificationDto = NotificationConverter.notificationDto(notification);
            emitterRepository.saveEventCache(key, notification);
            sendNotification(emitter, eventId, key, notificationDto);
        });
    }
    @Override
    public void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) { // (4)
        try {
            logger.info("Sending eventId: {}, data: {}", eventId, data);
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name("sse")
                    .data(data)
            );
        } catch (IOException exception) {
            logger.error("SSE 전송 실패: ", exception);
            emitterRepository.deleteById(emitterId);
        }
    }

    @Override
    public Notification patchNotificationRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationException(ErrorStatus.NOTIFICATION_NOT_FOUND));

        notification.notificationReadTrue();

        return notificationRepository.save(notification);
    }

    @Override
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationException(ErrorStatus.NOTIFICATION_NOT_FOUND));

        notificationRepository.delete(notification);
    }
}
