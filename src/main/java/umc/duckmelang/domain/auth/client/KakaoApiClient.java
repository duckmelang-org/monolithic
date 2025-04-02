package umc.duckmelang.domain.auth.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import umc.duckmelang.domain.auth.dto.kakao.KakaoTokenResponse;
import umc.duckmelang.domain.auth.dto.kakao.KakaoUserInfoResponse;

@Component
@RequiredArgsConstructor
public class KakaoApiClient { // Kakao API 를 호출하기 위한 전용 class
    private final WebClient webClient;

    private static final String USER_INFO_URI ="https://kapi.kakao.com/v2/user/me";
    private static final String TOKEN_REQUEST_URI = "https://kauth.kakao.com/oauth/token";

    @Value("${kakao.client-id}")
    private String kakaoApiKey;

    @Value("${kakao.client-secret}")
    private String kakaoSecret;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    // 1. 인가 코드 -> Access Token
    public String getAccessToken(String code) {
        return webClient.post()
                .uri(TOKEN_REQUEST_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=authorization_code"
                        + "&client_id=" + kakaoApiKey
                        + "&client_secret=" + kakaoSecret
                        + "&redirect_uri=" + redirectUri
                        + "&code=" + code)
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .map(KakaoTokenResponse::accessToken)
                .block();
    }

    // 2. Access Token -> 사용자 정보 요청
    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        return webClient.get()
                .uri(USER_INFO_URI)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .retrieve()
                .bodyToMono(KakaoUserInfoResponse.class)
                .block();
    }
}
