package umc.duckmelang.domain.auth.refreshToken;

import org.springframework.stereotype.Service;

@Service
public interface RefreshTokenService {
    void saveRefreshToken(String refreshToken, Long memberId);
    void removeRefreshToken(Long memberId);
    Long validateRefreshToken(String refreshToken);
}

