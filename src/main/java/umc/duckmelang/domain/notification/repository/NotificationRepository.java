package umc.duckmelang.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.notification.domain.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n JOIN FETCH n.receiver nr WHERE nr.id = :memberId")
    List<Notification> findAllByReceiverId(@Param("memberId") Long memberId);

    void deleteAllBySender(Member sender);
}
