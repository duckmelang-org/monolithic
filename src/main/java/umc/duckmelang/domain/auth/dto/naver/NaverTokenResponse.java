package umc.duckmelang.domain.auth.dto.naver;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NaverTokenResponse(@JsonProperty("access_token") String accessToken,
                                 @JsonProperty("token_type") String tokenType,
                                 @JsonProperty("expires_in") String expiresIn
) {}
