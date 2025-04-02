package umc.duckmelang.domain.auth.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import umc.duckmelang.domain.auth.dto.kakao.KakaoUserInfoResponse;

@RequiredArgsConstructor
@Component
public class KakaoApiClient { // Kakao API 를 호출하기 위한 전용 class
    private final WebClient webClient;
    private static final String USER_INFO_URI ="https://kapi.kakao.com/v2/user/me";

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

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
