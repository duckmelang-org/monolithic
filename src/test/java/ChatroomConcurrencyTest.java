import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.Redisson;
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

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomCommandServiceConcurrencyTest {
    private RedissonClient redissonClient;

    @Mock
    TransactionProceedAspect proceedAspect;

    private ChatRoomCommandService chatRoomCommandService;

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

        // 실제 RedissonClient 생성
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost:6379"); // 로컬 Redis 서버 주소
        redissonClient = Redisson.create(config);

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
    @DisplayName("동시 요청 시 Redisson 락이 동시성 문제를 방지하는지 확인")
    void shouldPreventConcurrencyIssueWithRedissonLock() throws Throwable {
        // Given
        final int numberOfThreads = 10;
        final AtomicInteger proceedCount = new AtomicInteger(0);

        // proceedAspect가 호출될 때마다 카운트 증가
        doAnswer(invocation -> {
            proceedCount.incrementAndGet();
            Thread.sleep(100); // 약간의 지연
            return invocation.getArgument(0);
        }).when(proceedAspect).proceed(any());

        // When: 여러 스레드에서 동시에 요청
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    barrier.await(); // 모든 스레드가 동시에 시작
                    chatRoomCommandService.createChatRoom(requestDto);
                } catch (Exception e) {
                    // 예외 처리 (락 획득 실패 등)
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        // Then
        assertEquals(1, proceedCount.get(), "Redisson 락이 있으면 한 번만 실행되어야 함");
    }
}