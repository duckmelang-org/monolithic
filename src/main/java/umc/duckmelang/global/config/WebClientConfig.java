package umc.duckmelang.global.config;

import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ReactorResourceFactory;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.function.Function;

@Configuration
public class WebClientConfig {
    /**
     *  Netty HTTP 클라이언트 설정
     *  WebClient에서 사용하는 Netty의 내부 리소스를 커스터마이징
     */
    @Bean
    public ReactorResourceFactory resourceFactory() {
        ReactorResourceFactory resourceFactory = new ReactorResourceFactory();
        resourceFactory.setUseGlobalResources(false); // 전역 리소스 풀 사용하지 않고 독립적인 리소스 사용
        return resourceFactory;
    }

    /**
     * WebClient Bean 등록
     * 외부 API에 연결할 대 사용할 WebClient
     * 타임아웃 및 커넥션 설정을 커스터마이징
     */
    @Bean
    public WebClient webClient() {
        Function<HttpClient,HttpClient> mapper = client ->
            HttpClient.create()
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // 연결 타임아웃 5초
                    .responseTimeout(Duration.ofSeconds(10)) // 응답 타임아웃 10초
                    .doOnConnected(conn -> conn
                            .addHandlerLast(new ReadTimeoutHandler(15)) // 읽기 타임아웃
                    );

            // HttpClient 로 ReactorClientHttpConnector 생성
            ClientHttpConnector connector = new ReactorClientHttpConnector(resourceFactory(), mapper);

            // WebClient 생성
            return WebClient.builder()
                    .clientConnector(connector)
                    .build();
    }
}
