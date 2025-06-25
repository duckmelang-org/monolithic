package umc.duckmelang.domain.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.auth.dto.response.LoginResponse;
import umc.duckmelang.domain.auth.service.strategy.SocialLoginStrategy;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.enums.LoginType;
import umc.duckmelang.domain.member.domain.enums.MemberStatus;
import umc.duckmelang.domain.member.domain.enums.Role;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.global.apipayload.exception.MemberException;
import umc.duckmelang.global.apipayload.exception.TokenException;
import umc.duckmelang.domain.auth.refreshToken.RefreshTokenServiceImpl;
import umc.duckmelang.domain.auth.jwt.JwtTokenProvider;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.AuthException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final MemberRepository memberRepository;
    private final Map<String, SocialLoginStrategy> loginStrategyMap;

    // 자체 로그인
    @Transactional
    public LoginResponse login(String email, String password){
        try{
            Authentication authentication = authenticate(email, password);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            Long memberId = userDetails.getMemberId();
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

            String accessToken = jwtTokenProvider.generateAccessToken(memberId, member.getRole().name());
            String refreshToken = jwtTokenProvider.generateRefreshToken(memberId, member.getRole().name());
            refreshTokenService.saveRefreshToken(refreshToken, memberId);

            return new LoginResponse(memberId, accessToken, refreshToken, member.isProfileComplete());

        } catch (UsernameNotFoundException e) {
            throw new AuthException(ErrorStatus.AUTH_USER_NOT_FOUND);
        } catch (BadCredentialsException e) {
            throw new AuthException(ErrorStatus.AUTH_INVALID_CREDENTIALS);
        }
    }

    // 소셜 로그인 처리
    @Transactional
    public LoginResponse socialLogin(LoginType loginType, String accessToken){
        SocialLoginStrategy strategy = loginStrategyMap.get(loginType.name());
        if(strategy == null){
            throw new AuthException(ErrorStatus.AUTH_INVALID_CREDENTIALS);
        }
        return strategy.login(accessToken);
    }

    // 토큰 재발급
    @Transactional
    public LoginResponse reissue(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new TokenException(ErrorStatus.MISSING_TOKEN);
        }
        // RefreshToken 유효성 확인 및 memberId 추출
        Long memberId = refreshTokenService.validateRefreshToken(refreshToken);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

        String role = jwtTokenProvider.getRoleFromToken(refreshToken);

        // 새 토큰 발급 및 저장
        String newAccessToken = jwtTokenProvider.generateAccessToken(memberId, role);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(memberId, role);

        refreshTokenService.saveRefreshToken(newRefreshToken, memberId);
        return new LoginResponse(memberId, newAccessToken, newRefreshToken, member.isProfileComplete());
    }

//    // 사용자 로그아웃 - RefreshToken 삭제
//    @Transactional
//    public void logout(Long memberId) {
//        // redis 에서 RefreshToken 삭제
//        refreshTokenService.removeRefreshToken(memberId);
//    }

    // 이메일/비밀번호 기반 사용자 인증
    private Authentication authenticate(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManager.authenticate(authenticationToken);
    }
}
