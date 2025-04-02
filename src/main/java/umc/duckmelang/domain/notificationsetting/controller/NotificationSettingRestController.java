package umc.duckmelang.domain.notificationsetting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.duckmelang.domain.notificationsetting.converter.NotificationSettingConverter;
import umc.duckmelang.domain.notificationsetting.domain.NotificationSetting;
import umc.duckmelang.domain.notificationsetting.dto.NotificationSettingRequestDto;
import umc.duckmelang.domain.notificationsetting.dto.NotificationSettingResponseDto;
import umc.duckmelang.domain.notificationsetting.service.NotificationSettingCommandService;
import umc.duckmelang.domain.notificationsetting.service.NotificationSettingQueryService;
import umc.duckmelang.global.apipayload.annotations.CommonApiResponses;
import umc.duckmelang.global.apipayload.ApiResponse;
import umc.duckmelang.domain.auth.user.CustomUserDetails;

@RestController
@RequestMapping("/notifications/setting")
@Tag(name="Notification Settings", description = "알림 설정 관련 API")
@RequiredArgsConstructor
public class NotificationSettingRestController {
    private final NotificationSettingQueryService notificationSettingQueryService;
    private final NotificationSettingCommandService notificationSettingCommandService;

    @Operation(summary = "알림 설정 조회 API", description = "해당하는 멤버의 알림 설정을 조회합니다.")
    @GetMapping("")
    @CommonApiResponses
    public ApiResponse<NotificationSettingResponseDto.NotificationSettingDto> getNotificationSetting(@AuthenticationPrincipal CustomUserDetails userDetails){
        Long memberId = userDetails.getMemberId();
        NotificationSetting notificationSetting = notificationSettingQueryService.findNotificationSetting(memberId);
        return ApiResponse.onSuccess(NotificationSettingConverter.notificationSettingDto(notificationSetting, memberId));
    }

    @Operation(summary = "알림 설정 관리 API", description = "알림 설정을 변경합니다. 변경하지 않는 부분은 지우고, 변경할 부분만 써주세요(true/false 형식)")
    @PatchMapping("")
    @CommonApiResponses
    public ApiResponse<NotificationSettingResponseDto.NotificationSettingDto> patchNotificationSetting(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody NotificationSettingRequestDto.UpdateNotificationSettingRequestDto request){
        Long memberId = userDetails.getMemberId();
        notificationSettingCommandService.updateNotificationSetting(memberId, request);
        NotificationSetting notificationSetting = notificationSettingQueryService.findNotificationSetting(memberId);
        return ApiResponse.onSuccess(NotificationSettingConverter.notificationSettingDto(notificationSetting, memberId));
    }
}
