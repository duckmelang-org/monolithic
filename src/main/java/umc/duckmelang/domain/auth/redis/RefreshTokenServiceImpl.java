package umc.duckmelang.domain.auth.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.TokenException;
import umc.duckmelang.domain.auth.jwt.JwtTokenProvider;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    // RefreshToken 저장
    @Override
    public void saveRefreshToken(String refreshToken, Long memberId) {
        String key = getKey(memberId);
        Duration expiration = Duration.ofSeconds(refreshTokenExpiration);
        redisTemplate.opsForValue().set(key, refreshToken, expiration);
    }

    // RefreshToken 유효성 검증
    // 사용자가 보낸 RefreshToken 이 Redis 에 저장된 값과 같은지 비교
    @Override
    public Long validateRefreshToken(String refreshToken) {
        Long memberId = jwtTokenProvider.getMemberIdFromToken(refreshToken);
        String storedToken = redisTemplate.opsForValue().get(getKey(memberId));

        if (storedToken == null) {throw new TokenException(ErrorStatus.MISSING_TOKEN);}
        if (!storedToken.equals(refreshToken)) {throw new TokenException(ErrorStatus.INVALID_TOKEN);}
        return memberId;
    }

    @Override
    public void removeRefreshToken(Long memberId){
        redisTemplate.delete(getKey(memberId));
    }

    private String getKey(Long memberId) {
        return "refresh_token:" + memberId;
    }
}
