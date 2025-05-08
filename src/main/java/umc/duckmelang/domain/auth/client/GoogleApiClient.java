package umc.duckmelang.domain.auth.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import umc.duckmelang.domain.auth.dto.google.GoogleTokenResponse;
import umc.duckmelang.domain.auth.dto.google.GoogleUserInfoResponse;

@Component
@RequiredArgsConstructor
public class GoogleApiClient {// Google API 를 호출하기 위한 전용 class
    private final WebClient webClient;

    private static final String TOKEN_REQUEST_URI = "https://oauth2.googleapis.com/token";
    private static final String USER_INFO_URI = "https://www.googleapis.com/oauth2/v1/userinfo";

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    // 1. 인가 코드 -> Access Token
    public String getAccessToken(String code) {
        return webClient.post()
                .uri(TOKEN_REQUEST_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=authorization_code"
                        + "&client_id=" + clientId
                        + "&client_secret=" + clientSecret
                        + "&redirect_uri=" + redirectUri
                        + "&code=" + code)
                .retrieve()
                .bodyToMono(GoogleTokenResponse.class)
                .map(GoogleTokenResponse::accessToken)
                .block();
    }

    // 2. Access Token -> 사용자 정보 요청
    public GoogleUserInfoResponse getUserInfo(String accessToken) {
        return webClient.get()
                .uri(USER_INFO_URI)
                .headers(headers -> {
                    headers.setBearerAuth(accessToken);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .retrieve()
                .bodyToMono(GoogleUserInfoResponse.class)
                .block();
    }
}
