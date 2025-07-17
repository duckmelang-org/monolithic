package umc.duckmelang.domain.auth.kakao;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class KakaoApiClient {

    private final WebClient webClient = WebClient.create("https://kapi.kakao.com");

    public String getEmailFromAccessToken(String accessToken) {
        return webClient.get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoEmailResponse.class)
                .map(response -> response.kakaoAccount().email())
                .block();
    }

    private record KakaoEmailResponse(
            @JsonProperty("kakao_account") KakaoAccount kakaoAccount
    ) {
        private record KakaoAccount(
                String email
        ) {}
    }
}
