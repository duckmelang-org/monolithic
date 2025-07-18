package umc.duckmelang.domain.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.TokenException;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.domain.auth.user.CustomUserDetailsService;
import java.util.List;

/**
 * JWT를 검증하고 사용자 인증 객체 생성
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    // 토큰의 유효성 검증
    public void validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtTokenProvider.getKey())
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new TokenException(ErrorStatus.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new TokenException(ErrorStatus.INVALID_TOKEN);
        }
    }

    // JWT 토큰으로부터 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        Long memberId = jwtTokenProvider.getMemberIdFromToken(token);
        CustomUserDetails userDetails = customUserDetailsService.loadUserByMemberId(memberId);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // 요청 헤더에서 Bearer 토큰 추출
    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 웹소켓 세션에서 Bearer 토큰 추출
    public String extractToken(WebSocketSession session) {
        List<String> authHeaders = session.getHandshakeHeaders().get("Authorization");

        if (authHeaders != null && !authHeaders.isEmpty()) {
            String bearerToken = authHeaders.get(0);
            if (bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7);
            }
        }
        return null;
    }
}
