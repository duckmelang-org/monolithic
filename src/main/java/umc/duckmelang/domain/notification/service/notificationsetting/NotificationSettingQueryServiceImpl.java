package umc.duckmelang.domain.notification.service.notificationsetting;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.notification.domain.NotificationSetting;
import umc.duckmelang.domain.notification.repository.NotificationSettingRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.MemberException;
import umc.duckmelang.global.apipayload.exception.NotificationSettingException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationSettingQueryServiceImpl implements NotificationSettingQueryService {
    private final MemberRepository memberRepository;
    private final NotificationSettingRepository notificationSettingRepository;

    @Override
    @Transactional
    public NotificationSetting findNotificationSetting(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

        NotificationSetting notificationSetting = member.getNotificationSetting();
        // 알림 설정이 없다면, 새로운 기본 알림 설정을 생성하고 Member와 연결하여 저장
        if (notificationSetting == null) {
            NotificationSetting defaultSetting = createDefaultNotificationSetting();
            member.setNotificationSetting(defaultSetting);
            notificationSettingRepository.save(defaultSetting);
            return defaultSetting;
        }

        // 알림 설정이 이미 존재한다면 해당 설정을 반환
        return notificationSetting;
    }

    private NotificationSetting createDefaultNotificationSetting() {
        return NotificationSetting.builder()
                .chatNotificationEnabled(true)
                .requestNotificationEnabled(true)
                .reviewNotificationEnabled(true)
                .bookmarkNotificationEnabled(true)
                .build();
    }
}
