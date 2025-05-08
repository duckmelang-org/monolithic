package umc.duckmelang.domain.auth.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.enums.MemberStatus;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .filter(m -> m.getMemberStatus() != MemberStatus.DELETED)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorStatus.AUTH_USER_NOT_FOUND.getMessage()));
        return new CustomUserDetails(member);
    }

    public CustomUserDetails loadUserByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .filter(m -> m.getMemberStatus() != MemberStatus.DELETED)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorStatus.AUTH_USER_NOT_FOUND.getMessage()));
        return new CustomUserDetails(member);
    }
}
