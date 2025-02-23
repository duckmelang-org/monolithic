package umc.duckmelang.domain.auth.dto;

import lombok.*;

public class AuthResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
        private Long memberId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private Long memberId;
        private String email;
        private String provider;
        private String accessToken;
        private String refreshToken;
        private boolean profileComplete;
    }
}
