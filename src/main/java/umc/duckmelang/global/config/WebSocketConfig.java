package umc.duckmelang.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")         // 클라이언트 WebSocket 연결 엔드포인트
                .setAllowedOriginPatterns("*")
                .withSockJS();              // SockJS fallback 지원 (브라우저 호환성)
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");     // 구독 prefix: /sub/chat/{roomId}
        registry.setApplicationDestinationPrefixes("/pub"); // 발행 prefix: /pub/chat/{roomId}
    }
}
