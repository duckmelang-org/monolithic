package umc.duckmelang.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.duckmelang.domain.chat.domain.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr JOIN FETCH cr.application a JOIN FETCH a.member JOIN FETCH a.post JOIN FETCH a.post.member WHERE a.member.id = :memberId OR a.post.member.id = :memberId")
    List<ChatRoom> findAllByMemberId(@Param("memberId") Long memberId);
}
