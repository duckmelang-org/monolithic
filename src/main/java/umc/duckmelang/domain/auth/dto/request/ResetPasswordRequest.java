package umc.duckmelang.domain.auth.dto.request;

public record ResetPasswordRequest(
        String loginId,
        String newPassword) {}
