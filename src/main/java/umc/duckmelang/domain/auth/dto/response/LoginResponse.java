package umc.duckmelang.domain.auth.dto.response;

public record LoginResponse(Long memberId,
                            String accessToken) {
}
