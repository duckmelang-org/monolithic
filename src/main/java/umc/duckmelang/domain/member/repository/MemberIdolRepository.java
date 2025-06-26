package umc.duckmelang.domain.member.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.MemberIdol;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberIdolRepository extends JpaRepository<MemberIdol, Long> {
    @Query("SELECT m FROM MemberIdol m JOIN FETCH m.idolCategory WHERE m.member.id = :memberId")
    List<MemberIdol> findByMember(@Param("memberId")Long memberId);

    @Query("SELECT m FROM MemberIdol m WHERE m.member.id = :memberId AND m.idolCategory.id = :idolId")
    Optional<MemberIdol> findByMemberAndIdol(@Param("memberId") Long memberId, @Param("idolId") Long idolId);

    @Query("SELECT COUNT(m) > 0 FROM MemberIdol m WHERE m.member.id = :memberId AND m.idolCategory.id = :idolId")
    boolean existsByMemberIdAndIdolCategoryId(@Param("memberId") Long memberId, @Param("idolId") Long idolId);
}
