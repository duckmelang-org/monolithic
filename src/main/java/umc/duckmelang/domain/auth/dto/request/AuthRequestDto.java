package umc.duckmelang.domain.auth.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class AuthRequestDto {

    @Getter
    public static class RefreshTokenRequestDto{
        @NotNull
        private String refreshToken;
    }
}
