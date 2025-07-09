package umc.duckmelang.domain.admin.service;

import umc.duckmelang.domain.member.domain.Member;

public interface AdminCommandService {
    Member joinAdmin(String email);
    Member deleteAdmin(String email);

}
