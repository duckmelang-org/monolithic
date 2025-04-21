import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.chatroom.service.ChatRoomCommandService;
import umc.duckmelang.domain.chatroom.service.ChatRoomCommandServiceImpl;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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
        // Given: 채팅방이 존재하지 않는 상황을 가정
        when(chatRoomRepository.findByPostIdAndOtherMemberId(anyLong(), anyLong()))
                .thenReturn(Optional.empty()); // 항상 빈 값 반환

        // 저장 시 입력된 엔티티를 그대로 반환하도록 설정
        when(chatRoomRepository.save(any(ChatRoom.class)))
                .thenAnswer(invocation -> {
                    ChatRoom chatRoom = invocation.getArgument(0);
                    return chatRoom;
                });

        // When: 동시에 여러 스레드에서 채팅방 생성 요청
        int numberOfThreads = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger saveCount = new AtomicInteger(0);

        // 저장 메소드 호출 횟수를 카운트하기 위한 설정
        doAnswer(invocation -> {
            saveCount.incrementAndGet();
            return invocation.callRealMethod();
        }).when(chatRoomRepository).save(any(ChatRoom.class));

        // 여러 스레드에서 동시에 요청
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    chatRoomCommandService.createChatRoom(requestDto);
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드가 완료될 때까지 대기
        latch.await();
        executorService.shutdown();

        // Then: 채팅방 조회와 저장이 각각 요청 수만큼 호출되었는지 확인
        verify(chatRoomRepository, times(numberOfThreads)).findByPostIdAndOtherMemberId(anyLong(), anyLong());

        // save 메소드가 여러 번 호출되었다면 동시성 문제가 존재함을 의미
        assertEquals(numberOfThreads, saveCount.get(),
                "동시성 문제: 동일한 채팅방이 여러 번 생성되었습니다");
    }

    @Test
    @DisplayName("동기화를 적용한 채팅방 생성 메소드 테스트")
    void synchronizedChatRoomCreationTest() throws InterruptedException {
        // Given: 첫 번째 조회는 빈 값, 그 이후 조회에서는 이미 생성된 채팅방 반환
        ChatRoom existingChatRoom = ChatRoom.builder()
                .id(1L)
                .post(post)
                .otherMember(sender)
                .chatRoomStatus(ChatRoomStatus.ONGOING)
                .build();

        // AtomicInteger로 호출 횟수 추적
        AtomicInteger callCount = new AtomicInteger(0);

        // 첫 번째 호출만 빈 값 반환, 그 이후에는 existingChatRoom 반환
        when(chatRoomRepository.findByPostIdAndOtherMemberId(anyLong(), anyLong()))
                .thenAnswer(invocation -> {
                    if (callCount.getAndIncrement() == 0) {
                        return Optional.empty();
                    } else {
                        return Optional.of(existingChatRoom);
                    }
                });

        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(existingChatRoom);

        // 동기화된 서비스 메소드 사용 (테스트용 목업 생성)
        ChatRoomCommandService synchronizedService = new ChatRoomCommandServiceImpl(
                chatRoomRepository, memberRepository, postRepository) {
            @Override
            public synchronized ChatRoom createChatRoom(ChatMessageRequestDto.CreateChatMessageDto request) {
                return super.createChatRoom(request);
            }
        };

        // When: 동시에 여러 스레드에서 채팅방 생성 요청
        int numberOfThreads = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger saveCount = new AtomicInteger(0);

        // 저장 메소드 호출 횟수를 카운트
        doAnswer(invocation -> {
            saveCount.incrementAndGet();
            return existingChatRoom;
        }).when(chatRoomRepository).save(any(ChatRoom.class));

        // 여러 스레드에서 동시에 요청
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    synchronizedService.createChatRoom(requestDto);
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드가 완료될 때까지 대기
        latch.await();
        executorService.shutdown();

        // Then: 동기화로 인해 저장은 단 한 번만 이루어져야 함
        assertEquals(1, saveCount.get(),
                "동기화 적용 후에도 중복 저장이 발생했습니다");
    }
}