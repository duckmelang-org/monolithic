package umc.duckmelang.domain.auth.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Component
public class GoogleApiClient {
    private final WebClient webClient;
}
