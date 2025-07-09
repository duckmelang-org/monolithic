package umc.duckmelang.domain.admin.service;

import umc.duckmelang.domain.member.domain.Member;

public interface AdminCommandService {
    Member joinAdmin(String loginId);
    Member deleteAdmin(Long memberId);
    void deleteOtherMember(Long memberId);

}
