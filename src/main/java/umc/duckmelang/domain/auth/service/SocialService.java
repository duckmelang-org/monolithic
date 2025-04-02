package umc.duckmelang.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.enums.LoginType;
import umc.duckmelang.domain.member.domain.enums.MemberStatus;
import umc.duckmelang.domain.member.domain.enums.Role;
import umc.duckmelang.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class SocialService {
    private final MemberRepository memberRepository;

    // 소셜 로그인 시 공통 가입 로직
    // 이미 가입된 사용자는 반환
    // 없으면 새로 생성해서 반환
    public Member findOrCreate(String email, String oauthId, LoginType loginType){
        return memberRepository.findByEmail(email)
                .orElseGet(()-> createNewMember(email, oauthId, loginType));
    }

    private Member createNewMember(String email, String oauthId, LoginType loginType){
        Member newMember = Member.builder()
                .email(email)
                .oauthId(oauthId)
                .loginType(loginType)
                .role(Role.USER)
                .memberStatus(MemberStatus.ACTIVE)
                .build();
        return memberRepository.save(newMember);
    }
}
