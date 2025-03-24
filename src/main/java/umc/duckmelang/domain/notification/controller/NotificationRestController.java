package umc.duckmelang.domain.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import umc.duckmelang.domain.notification.converter.NotificationConverter;
import umc.duckmelang.domain.notification.domain.Notification;
import umc.duckmelang.domain.notification.dto.NotificationResponseDto;
import umc.duckmelang.domain.notification.service.NotificationCommandService;
import umc.duckmelang.domain.notification.service.NotificationQueryService;
import umc.duckmelang.global.apipayload.annotations.CommonApiResponses;
import umc.duckmelang.global.apipayload.ApiResponse;
import umc.duckmelang.global.security.user.CustomUserDetails;
import umc.duckmelang.global.validation.annotation.ExistNotification;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Validated
public class NotificationRestController {
    private final NotificationQueryService notificationQueryService;
    private final NotificationCommandService notificationCommandService;

    @Operation(summary = "알림 목록 조회 API", description = "지금까지의 모든 알림 내역을 보여줍니다. extraData는 postImage 또는 memberProfileImage을 반환합니다. postImage -> BOOKMARK일때, memberProfileImage -> MATE, REQUEST, REVIEW 일때.")
    @GetMapping("")
    @CommonApiResponses
    public ApiResponse<NotificationResponseDto.NotificationListDto> getNotificationList (@AuthenticationPrincipal CustomUserDetails userDetails){
        Long memberId = userDetails.getMemberId();
        List<Notification> NotificationList = notificationQueryService.getNotificationList(memberId);
        return ApiResponse.onSuccess(NotificationConverter.notificationListDto(NotificationList));
    }

    @Operation(summary = "실시간 알림 API", description = "프론트에서의 요청 없이 자동으로 통신합니다. Swagger로 테스트는 불가하여 Postman으로 테스트해보셔야 합니다. 통신 시 SSE 연결하셔야 합니다. lastEventId는 처음 연결할 땐 안 쓰셔도 되며, 두번째 연결부터 쓰시면 됩니다. 미전송 데이터 여부 확인용입니다.")
    @GetMapping(value ="/subscribe", produces = "text/event-stream")
    @CommonApiResponses
    public SseEmitter getRealtimeNotification (@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        return notificationQueryService.subscribe(memberId, lastEventId);
    }

    @Operation(summary = "알림 읽음 API", description = "알림의 isRead 속성을 false에서 true로 변경합니다")
    @PatchMapping(value = "/{notificationId}/read")
    @CommonApiResponses
    public ApiResponse<NotificationResponseDto.NotificationReadDto> fetchNotificationRead (@ExistNotification @PathVariable Long notificationId) {
        Notification notification = notificationCommandService.patchNotificationRead(notificationId);
        return ApiResponse.onSuccess(NotificationConverter.notificationReadDto(notification));
    }

    @Operation(summary = "알림 삭제 API", description = "알림을 삭제합니다. 현재 피그마엔 없지만 알림 목록 조회에서 있으면 좋을 것 같아 만들었습니다.")
    @DeleteMapping(value = "/{notificationId}")
    @CommonApiResponses
    public ApiResponse<String> deleteNotification (@ExistNotification @PathVariable Long notificationId) {
        notificationCommandService.deleteNotification(notificationId);
        return ApiResponse.onSuccess("해당 알림을 삭제했습니다.");
    }
}
