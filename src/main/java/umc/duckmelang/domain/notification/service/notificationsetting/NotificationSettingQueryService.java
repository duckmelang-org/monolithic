package umc.duckmelang.domain.notification.service.notificationsetting;

import umc.duckmelang.domain.notification.domain.NotificationSetting;

public interface NotificationSettingQueryService {
    NotificationSetting findNotificationSetting(Long memberId);
}
