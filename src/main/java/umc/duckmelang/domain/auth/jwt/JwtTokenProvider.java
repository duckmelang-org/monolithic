package umc.duckmelang.domain.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JWT 생성 및 기본 정보 제공 클래스
 */
@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-expiration}") long refreshTokenExpiration
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // JWT 토큰 생성
    public String createToken(Long memberId, String role, long expiration) {
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Access Token 생성
    public String generateAccessToken(Long memberId, String role) {
        return createToken(memberId, role, accessTokenExpiration);
    }

    // RefreshToken 생성
    public String generateRefreshToken(Long memberId, String role) {
        return createToken(memberId, role, refreshTokenExpiration);
    }

    // JWT에서 Claims 파싱
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // JWT에서 사용자 ID 추출
    public Long getMemberIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return Long.valueOf(claims.getSubject());
    }

    // JWT에서 사용자 Role 추출
    public String getRoleFromToken(String token){
        Claims claims = parseClaims(token);
        return claims.get("role", String.class);
    }

    // JWT 서명 키 반환
    public Key getKey() {
        return this.key;
    }
}
