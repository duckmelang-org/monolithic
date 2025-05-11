import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import org.redisson.config.Config;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import umc.duckmelang.domain.chatroom.service.ChatRoomCommandService;
import umc.duckmelang.domain.chatroom.service.TestChatRoomCommandServiceImpl;
import umc.duckmelang.global.redis.concurrency.RedissonLock;
import umc.duckmelang.global.redis.concurrency.aop.RedissonLockAspect;
import umc.duckmelang.global.redis.concurrency.aop.TransactionProceedAspect;
import umc.duckmelang.mongo.chatmessage.domain.enums.MessageType;
import umc.duckmelang.mongo.chatmessage.dto.ChatMessageRequestDto;
import umc.duckmelang.domain.chatroom.domain.ChatRoom;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedissonLockAOPTest {
    private ChatRoomCommandService chatRoomCommandService;

    @Mock
    private RLock rLock;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    TransactionProceedAspect proceedAspect;

    @Captor
    private ArgumentCaptor<String> lockKeyCaptor;

    private ChatMessageRequestDto.CreateChatMessageDto requestDto;

    @BeforeEach
    void setUp() throws Throwable {
        // 테스트 데이터 초기화
        Long postId = 1_000_000_000L;
        Long senderId = 2_000_000_000L;
        Long receiverId = 3_000_000_000L;

        requestDto = ChatMessageRequestDto.CreateChatMessageDto.builder()
                .postId(postId)
                .senderId(senderId)
                .receiverId(receiverId)
                .messageType(MessageType.TEXT)
                .text("테스트 코드")
                .build();

        // Redisson 모의 설정
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        try{
            when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        RedissonLockAspect lockAspect = new RedissonLockAspect(redissonClient, proceedAspect);
        AspectJProxyFactory factory = new AspectJProxyFactory(new TestChatRoomCommandServiceImpl());
        factory.addAspect(lockAspect);
        chatRoomCommandService = factory.getProxy();
    }

    @AfterEach
    void tearDown() {
        // 리소스 정리
        if (redissonClient != null && !redissonClient.isShutdown()) {
            redissonClient.shutdown();
        }
    }

    @Test
    @DisplayName("Redisson 락이 올바른 키로 획득되는지 확인")
    void shouldAcquireRedissonLockWithCorrectKey() {
        // Given
        String expectedLockKey = "lock:chatroom:" + requestDto.getPostId() + "-" + requestDto.getSenderId();
        // When
        chatRoomCommandService.createChatRoom(requestDto);

        // Then
        // 올바른 키로 락을 획득했는지 확인
        verify(redissonClient).getLock(lockKeyCaptor.capture());
        String actualKey = lockKeyCaptor.getValue();
        System.out.println("Actual captured key: " + actualKey);
        assertEquals(expectedLockKey, lockKeyCaptor.getValue(), "락 키가 예상과 다릅니다");

        // 락 획득 및 해제가 일어났는지 확인
        try {
            verify(proceedAspect).proceed(any());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}