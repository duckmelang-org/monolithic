package umc.duckmelang.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.duckmelang.domain.notification.domain.NotificationSetting;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {

}
