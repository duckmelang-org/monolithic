package umc.duckmelang.domain.member.service.member;

import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.dto.member.MemberSignUpDto;


public interface MemberCommandService {
    Member signupMember(MemberSignUpDto.SignupDto request);
}
