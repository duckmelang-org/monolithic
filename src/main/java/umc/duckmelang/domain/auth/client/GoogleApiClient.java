package umc.duckmelang.domain.auth.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Component
public class GoogleApiClient {// Google API 를 호출하기 위한 전용 class
    private final WebClient webClient;


}
