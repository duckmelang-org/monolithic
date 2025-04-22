package umc.duckmelang.domain.chatroom.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import umc.duckmelang.global.apipayload.exception.ChatRoomException;
import umc.duckmelang.global.concurrency.RedissonLock;
import umc.duckmelang.mongo.chatmessage.dto.ChatMessageRequestDto;
import umc.duckmelang.domain.chatroom.converter.ChatRoomConverter;
import umc.duckmelang.domain.chatroom.domain.ChatRoom;
import umc.duckmelang.domain.chatroom.repository.ChatRoomRepository;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.repository.PostRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.MemberException;
import umc.duckmelang.global.apipayload.exception.PostException;

@Service
@RequiredArgsConstructor
public class ChatRoomCommandServiceImpl implements ChatRoomCommandService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    // 채팅 생성 시, 채팅방이 있는지 조회하고, 없으면 채팅방을 생성하기 위한 ChatMessageCommandService에 사용된다.
    @Override
    @RedissonLock(key = "\"chatroom\".concat(#request.getReceiverId()).concat('-').concat(#request.getSenderId()).concat('#')" +
            ".concat(#request.getPostId())")
    public ChatRoom createChatRoom(ChatMessageRequestDto.CreateChatMessageDto request) {

        if(chatRoomRepository.findByPostIdAndOtherMemberId(request.getPostId(), request.getSenderId()).isPresent())
            throw new ChatRoomException(ErrorStatus.CHATROOM_ALREADY_EXISTS);

        // 요청 데이터의 필드가 유효한지 확인한다.
        Member receivedMember = memberRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

        Member sendingMember = memberRepository.findById(request.getSenderId())
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new PostException(ErrorStatus.POST_NOT_FOUND));

        // 채팅방을 생성한다.
        ChatRoom newChatRoom = ChatRoomConverter.toChatRoom(request, post, sendingMember);
        try {
            return chatRoomRepository.save(newChatRoom);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}