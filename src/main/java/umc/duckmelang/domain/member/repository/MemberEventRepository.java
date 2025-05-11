package umc.duckmelang.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.MemberEvent;

@Repository
public interface MemberEventRepository extends JpaRepository<MemberEvent, Long> {
    void deleteAllByMember(Member member);
}
