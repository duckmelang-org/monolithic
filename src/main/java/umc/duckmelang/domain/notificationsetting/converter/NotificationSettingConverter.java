package umc.duckmelang.domain.notificationsetting.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.notificationsetting.domain.NotificationSetting;
import umc.duckmelang.domain.notificationsetting.dto.NotificationSettingRequestDto;
import umc.duckmelang.domain.notificationsetting.dto.NotificationSettingResponseDto;

@Component
@RequiredArgsConstructor
public class NotificationSettingConverter {
    public static NotificationSettingResponseDto.NotificationSettingDto notificationSettingDto(NotificationSetting notificationSetting, Long memberId) {
        return NotificationSettingResponseDto.NotificationSettingDto.builder()
                .notificationSettingId(notificationSetting.getId())
                .memberId(memberId)
                .chatNotificationEnabled(notificationSetting.getChatNotificationEnabled())
                .requestNotificationEnabled(notificationSetting.getRequestNotificationEnabled())
                .reviewNotificationEnabled(notificationSetting.getReviewNotificationEnabled())
                .bookmarkNotificationEnabled(notificationSetting.getBookmarkNotificationEnabled())
                .build();
    }

    public static NotificationSetting updateNotificationSetting(NotificationSettingRequestDto.UpdateNotificationSettingRequestDto request) {
        NotificationSetting notificationSetting = NotificationSetting.builder()
                .chatNotificationEnabled(request.getChatNotificationEnabled())
                .requestNotificationEnabled(request.getRequestNotificationEnabled())
                .reviewNotificationEnabled(request.getReviewNotificationEnabled())
                .bookmarkNotificationEnabled(request.getBookmarkNotificationEnabled())
                .build();

        return notificationSetting;
    }
}
