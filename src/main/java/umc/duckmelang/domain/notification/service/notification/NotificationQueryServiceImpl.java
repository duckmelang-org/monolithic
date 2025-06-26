package umc.duckmelang.domain.notification.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import umc.duckmelang.domain.notification.domain.Notification;
import umc.duckmelang.domain.notification.repository.EmitterRepository;
import umc.duckmelang.domain.notification.repository.NotificationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryServiceImpl implements NotificationQueryService {
    @Value("${notification.timeout}")
    private Long defaultTimeout;

    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;
    private final NotificationCommandService notificationCommandService;

    @Override
    public List<Notification> getNotificationList(Long memberId){
        return notificationRepository.findAllByReceiverId(memberId);
    }

    @Override
    public SseEmitter subscribe(Long memberId, String lastEventId){
        String emitterId = makeTimeIncludeId(memberId); // (1-2)
        SseEmitter emitter =  new SseEmitter(defaultTimeout);
        // 새로운 Emitter 저장
        emitterRepository.save(emitterId, emitter);
        // (1-4) 연결 종료 및 타임아웃 시 삭제
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        // (1-5) 503 에러를 방지하기 위한 더미 이벤트 전송
        String eventId = makeTimeIncludeId(memberId);
        notificationCommandService.sendNotification(emitter, eventId, emitterId, "EventStream Created. [memberId=" + memberId + "]");
        // (1-6) 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (hasLostData(lastEventId)) {
            notificationCommandService.sendLostData(lastEventId, memberId, emitterId, emitter);
        }
        return emitter; // (1-7)
    }

    @Override
    public String makeTimeIncludeId(Long memberId) { // (3)
        return memberId + "_" + System.currentTimeMillis();
    }

    @Override
    public boolean hasLostData(String lastEventId) { // (5)
        return lastEventId != null && !lastEventId.isEmpty();
    }

    @Override
    public boolean existsById(Long notificationId) {
        return notificationRepository.existsById(notificationId);
    }



}
