package umc.duckmelang.domain.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.auth.dto.response.LoginResponse;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.global.apipayload.exception.MemberException;
import umc.duckmelang.global.apipayload.exception.TokenException;
import umc.duckmelang.domain.auth.refreshToken.RefreshTokenServiceImpl;
import umc.duckmelang.domain.auth.jwt.JwtTokenProvider;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 자체 로그인
    @Transactional
    public LoginResponse login(String loginId, String password){
        Authentication authentication = authenticate(loginId, password);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Long memberId = userDetails.getMemberId();
        Member member = findMemberOrThrow(memberId);

        String accessToken = jwtTokenProvider.generateAccessToken(memberId, member.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(memberId, member.getRole().name());
        refreshTokenService.saveRefreshToken(refreshToken, memberId);

        return new LoginResponse(memberId, accessToken, refreshToken, member.isProfileComplete());
    }

    // 토큰 재발급
    @Transactional
    public LoginResponse reissue(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new TokenException(ErrorStatus.MISSING_TOKEN);
        }
        // RefreshToken 유효성 확인 및 memberId 추출
        Long memberId = refreshTokenService.validateRefreshToken(refreshToken);
        Member member = findMemberOrThrow(memberId);

        String role = jwtTokenProvider.getRoleFromToken(refreshToken);

        // 새 토큰 발급 및 저장
        String newAccessToken = jwtTokenProvider.generateAccessToken(memberId, role);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(memberId, role);

        refreshTokenService.saveRefreshToken(newRefreshToken, memberId);
        return new LoginResponse(memberId, newAccessToken, newRefreshToken, member.isProfileComplete());
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

    public boolean isDuplicateLoginId(String loginId){
        return memberRepository.existsByLoginId(loginId);
    }

    public boolean isDuplicatePhoneNum(String phoneNum){
        return memberRepository.existsByPhoneNum(phoneNum);
    }

    public String findLoginIdByPhoneNum(String phoneNum){
        Member member = memberRepository.findByPhoneNum(phoneNum)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
        return member.getLoginId();
    }

    @Transactional
    public void addPhoneNum(String phoneNum, Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
        member.updatePhoneNum(phoneNum);
    }

    @Transactional
    public void resetPassword(String loginId, String newPassword){
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
        member.updatePassword(passwordEncoder.encode(newPassword));
    }
}
