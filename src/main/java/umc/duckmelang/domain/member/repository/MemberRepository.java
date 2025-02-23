package umc.duckmelang.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.enums.MemberStatus;
import umc.duckmelang.domain.notificationsetting.domain.NotificationSetting;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);


    @Query("SELECT n from Member m JOIN m.notificationSetting n where m.id = :memberId")
    Optional<NotificationSetting> findNotificationSettingByMemberId(Long memberId);
}
