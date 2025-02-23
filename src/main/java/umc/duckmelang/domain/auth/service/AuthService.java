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
import umc.duckmelang.global.redis.blacklist.BlacklistServiceImpl;
import umc.duckmelang.global.redis.refreshtoken.RefreshToken;
import umc.duckmelang.global.redis.refreshtoken.RefreshTokenServiceImpl;
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
    private final BlacklistServiceImpl blacklistService;
    private final JwtUtil jwtUtil;

   // 사용자 로그인
    @Transactional
    public AuthResponseDto.TokenResponse login(String email, String password){
        try{
            Authentication authentication = authenticate(email, password);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long memberId = userDetails.getMemberId();
            String accessToken = jwtTokenProvider.generateAccessToken(memberId);
            String refreshToken = jwtTokenProvider.generateRefreshToken(memberId);
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
        RefreshToken storedToken = refreshTokenService.validateAndGetRefreshToken(refreshToken);
        refreshTokenService.removeRefreshToken(refreshToken);
        Long memberId = storedToken.getMemberId();
        String newAccessToken = jwtTokenProvider.generateAccessToken(memberId);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(memberId);
        refreshTokenService.saveRefreshToken(newRefreshToken, memberId);
        return new AuthResponseDto.TokenResponse(newAccessToken, newRefreshToken, memberId);
    }

    // 사용자 로그아웃
    @Transactional
    public void logout(String accessToken) {
        if (jwtUtil.isTokenExpired(accessToken)) {
            throw new TokenException(ErrorStatus.INVALID_TOKEN);
        }
        long expiration = jwtUtil.getExpirationFromToken(accessToken);
        if (expiration > 0) {
            blacklistService.addToBlacklist(accessToken, expiration);
        } else {
            throw new TokenException(ErrorStatus.INVALID_TOKEN);
        }
    }

    // 사용자 인증
    private Authentication authenticate(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManager.authenticate(authenticationToken);
    }
}
