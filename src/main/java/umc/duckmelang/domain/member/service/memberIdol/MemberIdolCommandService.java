package umc.duckmelang.domain.member.service.memberIdol;

import umc.duckmelang.domain.member.domain.MemberIdol;

public interface MemberIdolCommandService {
    void deleteMemberIdol(Long memberId, Long idolId);
    MemberIdol addMemberIdol(Long memberId, Long idolId);
}
