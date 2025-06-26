package umc.duckmelang.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NotificationSettingResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NotificationSettingDto {
        private Long notificationSettingId;
        private Long memberId;
        private Boolean chatNotificationEnabled;
        private Boolean requestNotificationEnabled;
        private Boolean reviewNotificationEnabled;
        private Boolean bookmarkNotificationEnabled;

    }

}
