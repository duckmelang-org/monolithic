package umc.duckmelang.domain.admin.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.enums.Role;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.MemberException;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminCommandServiceImpl implements AdminCommandService {

    private final MemberRepository memberRepository;

    @Override
    public Member joinAdmin(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
        member.switchRole(Role.ADMIN);
        return member;
    }

    @Override
    public Member deleteAdmin(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
        member.switchRole(Role.USER);
        return member;

    }
}




