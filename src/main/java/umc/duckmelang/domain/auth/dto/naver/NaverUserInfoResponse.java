package umc.duckmelang.domain.auth.dto.naver;

public record NaverUserInfoResponse(NaverResponse response) {

    public record NaverResponse(
            String id,
            String email
    ) {}
}