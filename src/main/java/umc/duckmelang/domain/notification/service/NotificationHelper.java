package umc.duckmelang.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.member.domain.MemberProfileImage;
import umc.duckmelang.domain.member.service.profileImage.MemberProfileImageQueryService;
import umc.duckmelang.domain.notification.domain.enums.NotificationType;
import umc.duckmelang.domain.notification.service.notification.NotificationCommandService;
import umc.duckmelang.domain.notification.service.notificationsetting.NotificationSettingQueryService;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.MemberException;
import umc.duckmelang.global.apipayload.exception.MemberProfileImageException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationHelper {
    private final NotificationCommandService notificationCommandService;
    private final NotificationSettingQueryService notificationSettingQueryService;
    private final MemberRepository memberRepository;
    private final MemberProfileImageQueryService memberProfileImageQueryService;

    public void sendNotification(Long senderId, Long receiverId, NotificationType type, String message) {
        // 1. 알림 설정 확인 (꺼져 있으면 바로 리턴)
        if (!notificationSettingQueryService.findNotificationSetting(receiverId).getRequestNotificationEnabled()) {
            return; // 알림 설정이 꺼져 있으면 바로 종료
        }

        // 프로필 이미지 가져오기
        Optional<MemberProfileImage> profileImageOptional = memberProfileImageQueryService.getLatestPublicMemberProfileImage(senderId);
        String profileImageUrl = profileImageOptional
                .map(MemberProfileImage::getMemberImage)  // memberImage 필드를 가져옴
                .orElseThrow(() -> new MemberProfileImageException(ErrorStatus.MEMBER_PROFILE_IMAGE_NOT_FOUND));

        // 발신자, 수신자 조회
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

        notificationCommandService.send(receiver, sender, type, message, profileImageUrl);

    }
}
