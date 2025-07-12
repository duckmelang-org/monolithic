package umc.duckmelang.domain.admin.service;

import umc.duckmelang.domain.member.domain.Member;

import java.util.List;

public interface AdminQueryService {
    List<Member> getAdmin();
}
