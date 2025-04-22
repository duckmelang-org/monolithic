import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.verification.TooManyActualInvocations;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.chatroom.service.ChatRoomCommandService;
import umc.duckmelang.domain.chatroom.service.ChatRoomCommandServiceImpl;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.ChatRoomException;
import umc.duckmelang.mongo.chatmessage.domain.enums.MessageType;
import umc.duckmelang.mongo.chatmessage.dto.ChatMessageRequestDto;
import umc.duckmelang.domain.chatroom.domain.ChatRoom;
import umc.duckmelang.domain.chatroom.repository.ChatRoomRepository;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.repository.PostRepository;
import umc.duckmelang.domain.chatroom.domain.enums.ChatRoomStatus;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * 채팅방 생성 시 발생할 수 있는 동시성 문제를 테스트하는 클래스
 */

@ExtendWith(MockitoExtension.class)
class ChatRoomCommandServiceConcurrencyTest {
    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private ChatRoomCommandServiceImpl chatRoomCommandService;

    private Member sender;
    private Member receiver;
    private Post post;
    private ChatMessageRequestDto.CreateChatMessageDto requestDto;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        Long postId = 1_000_000_000L;
        Long senderId = 2_000_000_000L;
        Long receiverId = 3_000_000_000L;

        sender = Member.builder().id(senderId).build();
        receiver = Member.builder().id(receiverId).build();
        post = Post.builder().id(postId).member(receiver).build();

        requestDto = ChatMessageRequestDto.CreateChatMessageDto.builder()
                .postId(postId)
                .senderId(senderId)
                .receiverId(receiverId)
                .messageType(MessageType.TEXT)
                .text("테스트 코드")
                .build();

        // Mock repository 기본 설정
        when(memberRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(memberRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
    }

    @Test
    @DisplayName("동시 요청 시 채팅방 중복 생성 이슈 테스트")
    void concurrentChatRoomCreationTest() throws InterruptedException {
        // Given
        final int numberOfThreads = 100;

        // Thread-safe 변수들 준비
        final AtomicInteger findCallCount = new AtomicInteger(0);
        final AtomicInteger successCount = new AtomicInteger(0);
        final AtomicReference<ChatRoom> savedChatRoom = new AtomicReference<>();

        // CyclicBarrier를 사용하여 스레드들이 동시에 실행되도록 조정
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(numberOfThreads);

        // 완료 신호를 위한 CountDownLatch
        final CountDownLatch completionLatch = new CountDownLatch(numberOfThreads);

        // 첫 번째 조회는 빈 결과를 반환하고, 그 이후에는 존재하는 채팅방을 반환하도록 설정
        when(chatRoomRepository.findByPostIdAndOtherMemberId(anyLong(), anyLong()))
                .thenAnswer(invocation -> {
                    int currentCall = findCallCount.getAndIncrement();

                    // 첫 번째 호출에만 빈 Optional 반환
                    if (currentCall == 0) {
                        return Optional.empty();
                    }

                    // 이미 채팅방이 생성된 경우, 저장된 채팅방 반환
                    ChatRoom chatRoom = savedChatRoom.get();
                    if (chatRoom != null) {
                        return Optional.of(chatRoom);
                    }

                    // 아직 저장되지 않았으면 빈 값 반환 (이 경우는 거의 발생하지 않음)
                    return Optional.empty();
                });

        // 채팅방 저장 시 AtomicReference에 저장하고 그대로 반환
        when(chatRoomRepository.save(any(ChatRoom.class)))
                .thenAnswer(invocation -> {
                    ChatRoom chatRoom = invocation.getArgument(0);
                    savedChatRoom.set(chatRoom);
                    return chatRoom;
                });

        // When: 여러 스레드에서 동시에 요청
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    // 모든 스레드가 이 지점에서 대기
                    cyclicBarrier.await();
                    chatRoomCommandService.createChatRoom(requestDto);
                    successCount.incrementAndGet();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    completionLatch.countDown();
                }
            });
        }

        // 완료 대기
        boolean completed = completionLatch.await(10, TimeUnit.SECONDS);
        assertTrue(completed, "테스트가 제한 시간 내에 완료되지 않았습니다");

        // 리소스 정리
        executorService.shutdown();
        boolean terminated = executorService.awaitTermination(5, TimeUnit.SECONDS);
        assertTrue(terminated, "ExecutorService가 시간 내에 종료되지 않았습니다");

        // Then
        // 1. ChatRoom 저장 호출 횟수는 정확히 1번이어야 함 (중복 생성 방지)
        try{
            verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
        } catch (TooManyActualInvocations e){
            fail("채팅방 생성 요청이 중복되고 있습니다.");
        }

        // 2. 성공 횟수도 정확히 1번이어야 함
        assertEquals(1, successCount.get(), "성공 횟수가 1이 아닙니다");
    }
}