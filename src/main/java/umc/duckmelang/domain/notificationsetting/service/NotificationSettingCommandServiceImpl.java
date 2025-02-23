package umc.duckmelang.domain.notificationsetting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.notificationsetting.domain.NotificationSetting;
import umc.duckmelang.domain.notificationsetting.dto.NotificationSettingRequestDto;
import umc.duckmelang.domain.notificationsetting.repository.NotificationSettingRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.NotificationSettingException;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationSettingCommandServiceImpl implements NotificationSettingCommandService {
    public final MemberRepository memberRepository;

    @Override
    public void updateNotificationSetting(Long memberId, NotificationSettingRequestDto.UpdateNotificationSettingRequestDto request) {
        NotificationSetting setting = memberRepository.findNotificationSettingByMemberId(memberId)
                .orElseThrow(() -> new NotificationSettingException(ErrorStatus.NOTIFICATION_SETTING_NOT_FOUND));

        if (request.getChatNotificationEnabled() != null) {
            setting.setChatNotificationEnabled(request.getChatNotificationEnabled());
        }
        if (request.getRequestNotificationEnabled() != null) {
            setting.setRequestNotificationEnabled(request.getRequestNotificationEnabled());
        }
        if (request.getReviewNotificationEnabled() != null) {
            setting.setReviewNotificationEnabled(request.getReviewNotificationEnabled());
        }
        if (request.getBookmarkNotificationEnabled() != null) {
            setting.setBookmarkNotificationEnabled(request.getBookmarkNotificationEnabled());
        }

    }
}
