package umc.duckmelang.domain.member.converter;

import org.springframework.stereotype.Component;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.type.Role;
import umc.duckmelang.domain.member.dto.member.MemberSignUpDto;

@Component
public class MemberConverter {

    public static Member toMember(MemberSignUpDto.SignupDto request, String encodedPassword){
        return Member.builder()
                .loginId(request.getLoginId())
                .password(encodedPassword)
                .role(Role.USER)
                .build();
    }

    public static MemberSignUpDto.SignupResultDto toSignupResultDto(Member member){
        return MemberSignUpDto.SignupResultDto.builder()
                .memberId(member.getId())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
