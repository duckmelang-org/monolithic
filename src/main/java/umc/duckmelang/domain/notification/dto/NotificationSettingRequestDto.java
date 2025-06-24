package umc.duckmelang.domain.notification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class NotificationSettingRequestDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpdateNotificationSettingRequestDto{
        private Boolean chatNotificationEnabled;
        private Boolean requestNotificationEnabled;
        private Boolean reviewNotificationEnabled;
        private Boolean bookmarkNotificationEnabled;
    }

}
