package umc.duckmelang.domain.chatroom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import umc.duckmelang.global.apipayload.exception.ChatRoomException;
import umc.duckmelang.global.redis.concurrency.RedissonLock;
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
@Log4j2
@Service
@RequiredArgsConstructor
public class ChatRoomCommandServiceImpl implements ChatRoomCommandService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    // 채팅 생성 시, 채팅방이 있는지 조회하고, 없으면 채팅방을 생성하기 위한 ChatMessageCommandService에 사용된다.
    @Override
    @RedissonLock(key = "'chatroom:'.concat(#request.getPostId()).concat('-').concat(#request.getSenderId())")
    public ChatRoom createChatRoom(ChatMessageRequestDto.CreateChatMessageDto request) {

        Member receiver = memberRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

        Member sender = memberRepository.findById(request.getSenderId())
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new PostException(ErrorStatus.POST_NOT_FOUND));

        return chatRoomRepository.findByPostIdAndOtherMemberId(post.getId(), post.getMember() == sender ? receiver.getId() : sender.getId())
                .orElseGet(() -> {
                    ChatRoom newChatRoom = ChatRoomConverter.toChatRoom(request, post, post.getMember() == sender ? receiver : sender);
                    return chatRoomRepository.save(newChatRoom);
                });
    }
}