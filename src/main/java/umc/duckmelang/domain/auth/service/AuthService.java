package umc.duckmelang.domain.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.auth.dto.AuthResponseDto;
import umc.duckmelang.global.apipayload.exception.TokenException;
import umc.duckmelang.global.security.redis.RefreshTokenServiceImpl;
import umc.duckmelang.global.security.jwt.JwtTokenProvider;
import umc.duckmelang.global.security.jwt.JwtUtil;
import umc.duckmelang.global.security.user.CustomUserDetails;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.AuthException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final JwtUtil jwtUtil;

   // 사용자 로그인 - 인증 및 토큰 발급
    @Transactional
    public AuthResponseDto.TokenResponse login(String email, String password){
        try{
            Authentication authentication = authenticate(email, password);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long memberId = userDetails.getMemberId();
            String role = userDetails.getRole().name();

            String accessToken = jwtTokenProvider.generateAccessToken(memberId, role);
            String refreshToken = jwtTokenProvider.generateRefreshToken(memberId, role);
            refreshTokenService.saveRefreshToken(refreshToken, memberId);

            return new AuthResponseDto.TokenResponse(accessToken, refreshToken, memberId);
        } catch (UsernameNotFoundException e) {
            throw new AuthException(ErrorStatus.AUTH_USER_NOT_FOUND);
        } catch (BadCredentialsException e) {
            throw new AuthException(ErrorStatus.AUTH_INVALID_CREDENTIALS);
        }
    }

    // 토큰 재발급
    @Transactional
    public AuthResponseDto.TokenResponse reissue(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new TokenException(ErrorStatus.MISSING_TOKEN);
        }
        // RefreshToken 유효성 확인 및 memberId 추출
        Long memberId = refreshTokenService.validateRefreshToken(refreshToken);
        String role = jwtTokenProvider.getRoleFromToken(refreshToken);

        // 새 토큰 발급 및 저장
        String newAccessToken = jwtTokenProvider.generateAccessToken(memberId, role);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(memberId, role);

        refreshTokenService.saveRefreshToken(newRefreshToken, memberId);
        return new AuthResponseDto.TokenResponse(newAccessToken, newRefreshToken, memberId);
    }

    // 사용자 로그아웃 - RefreshToken 삭제
    @Transactional
    public void logout(Long memberId) {
        // redis 에서 RefreshToken 삭제
        refreshTokenService.removeRefreshToken(memberId);
    }

    // 이메일/비밀번호 기반 사용자 인증
    private Authentication authenticate(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManager.authenticate(authenticationToken);
    }
}
