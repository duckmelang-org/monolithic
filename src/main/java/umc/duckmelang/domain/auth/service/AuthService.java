package umc.duckmelang.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.auth.dto.response.LoginResponse;
import umc.duckmelang.domain.auth.jwt.JwtTokenProvider;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.MemberException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    // 자체 로그인
    @Transactional
    public LoginResponse login(String loginId, String password){
        Authentication authentication = authenticate(loginId, password);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Long memberId = userDetails.getMemberId();
        Member member = findMemberOrThrow(memberId);

        String accessToken = jwtTokenProvider.generateAccessToken(memberId, member.getRole().name());

        return new LoginResponse(memberId, accessToken);
    }

    // 아이디/비밀번호 기반 사용자 인증
    private Authentication authenticate(String loginId, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginId, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    private Member findMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
    }
}
