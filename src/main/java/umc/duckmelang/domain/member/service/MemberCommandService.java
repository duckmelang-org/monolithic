package umc.duckmelang.domain.member.service;

import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.dto.MemberSignUpDto;

public interface MemberCommandService {
    Member signupMember(MemberSignUpDto.SignupDto request);
}
