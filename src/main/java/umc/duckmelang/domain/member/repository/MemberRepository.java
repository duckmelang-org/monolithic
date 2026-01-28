package umc.duckmelang.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.enums.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findByPhoneNum(String phoneNum);
    boolean existsByLoginId(String loginId);
    boolean existsByNickname(String nickname);
    boolean existsByPhoneNum(String phoneNum);
    List<Member> findByRole(Role role);
}
