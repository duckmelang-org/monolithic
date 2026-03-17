package umc.duckmelang.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.auth.jwt.JwtUtil;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    /**
     * STOMP 프레임이 인바운드 채널에 도달하기 전에 가로챔
     * CONNECT 프레임에서만 JWT 검증 수행
     * → 이후 모든 SEND/SUBSCRIBE 프레임은 연결 시 설정된 Principal 재사용
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                String token = bearerToken.substring(7);
                jwtUtil.validateToken(token);
                Authentication authentication = jwtUtil.getAuthentication(token);
                accessor.setUser(authentication);  // 이후 모든 메시지에서 Principal로 접근 가능
            }
        }

        return message;
    }
}
