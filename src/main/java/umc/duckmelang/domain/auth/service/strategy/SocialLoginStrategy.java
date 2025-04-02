package umc.duckmelang.domain.auth.service.strategy;

import org.springframework.stereotype.Service;
import umc.duckmelang.domain.auth.dto.response.LoginResponse;

@Service
public interface SocialLoginStrategy {
    LoginResponse login(String accessToken);
}
