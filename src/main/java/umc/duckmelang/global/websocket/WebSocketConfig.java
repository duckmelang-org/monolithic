package umc.duckmelang.global.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket   // WebSocket 사용 활성화
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketChatHandler webSocketChatHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                // 클라이언트가 웹소켓 연결을 하고자 /wss/chat 경로로 연결을 시도하면 WebSocket 연결을 허용한다.
                // ChatWebSocketHandler 클래스에서 이를 처리한다.
                .addHandler(webSocketChatHandler, "/wss/chat")
                // CORS 설정
                // 일단은 개발을 위하여 접속을 시도하는 모든 도메인, IP의 WebSocket 연결을 허용한다.
                // 추후 특정 도메인만 허용하도록 설정히야 한다.
                .setAllowedOrigins("*");
    }


}
