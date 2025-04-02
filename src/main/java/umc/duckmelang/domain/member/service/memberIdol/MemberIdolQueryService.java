package umc.duckmelang.domain.member.service.memberIdol;

import umc.duckmelang.domain.member.domain.MemberIdol;

import java.util.List;

public interface MemberIdolQueryService {
    List<MemberIdol> getIdolListByMember(Long memberId);
}
