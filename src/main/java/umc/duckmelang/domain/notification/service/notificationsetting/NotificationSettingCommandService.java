package umc.duckmelang.domain.notification.service.notificationsetting;

import umc.duckmelang.domain.notification.dto.NotificationSettingRequestDto;

public interface NotificationSettingCommandService {
    void updateNotificationSetting(Long memberId, NotificationSettingRequestDto.UpdateNotificationSettingRequestDto request);
}
