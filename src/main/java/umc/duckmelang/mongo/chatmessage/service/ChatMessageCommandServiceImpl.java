package umc.duckmelang.mongo.chatmessage.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.repository.PostRepository;
import umc.duckmelang.domain.uuid.service.UuidService;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.MemberException;
import umc.duckmelang.global.apipayload.exception.PostException;
import org.springframework.web.multipart.MultipartFile;
import umc.duckmelang.domain.uuid.domain.Uuid;
import umc.duckmelang.domain.uuid.repository.UuidRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.ChatMessageException;
import umc.duckmelang.global.aws.AmazonS3Manager;
import umc.duckmelang.mongo.chatmessage.converter.ChatMessageConverter;
import umc.duckmelang.mongo.chatmessage.domain.ChatMessage;
import umc.duckmelang.mongo.chatmessage.dto.ChatMessageRequestDto;
import umc.duckmelang.domain.chatroom.converter.ChatRoomConverter;
import umc.duckmelang.domain.chatroom.domain.ChatRoom;
import umc.duckmelang.domain.chatroom.repository.ChatRoomRepository;
import umc.duckmelang.domain.chatroom.service.ChatRoomCommandService;
import umc.duckmelang.mongo.chatmessage.repository.ChatMessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMessageCommandServiceImpl implements ChatMessageCommandService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomCommandService chatRoomCommandService;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final AmazonS3Manager s3Manager;
    private final UuidService uuidService;


    @Override
    @Transactional
    public ChatMessage processMessage(ChatMessageRequestDto.@Valid CreateChatMessageDto request){
        // 1. 채팅방이 있는지 조회하고, 없으면 생성한다.
        Post post = postRepository.findById(request.getPostId()).orElseThrow(()->new PostException(ErrorStatus.POST_NOT_FOUND));
        Member otherMember = memberRepository.findById(request.getReceiverId()).orElseThrow(()->new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
        Member member = memberRepository.findById(request.getSenderId()).orElseThrow(()->new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

        ChatRoom chatRoom = chatRoomRepository.findByPostIdAndOtherMemberId(request.getPostId(), request.getReceiverId())
                .or(() -> chatRoomRepository.findByPostIdAndOtherMemberId(request.getPostId(), request.getSenderId()))
                .orElseGet(() -> {
                    ChatRoom newChatRoom = ChatRoomConverter.toChatRoom(request, post, otherMember);
                    return chatRoomRepository.save(newChatRoom); // 채팅방 생성 후 DB에 저장
                });

        // 2. 메세지 타입에 따라 로직 분기
        ChatMessage newChatMessage;
        switch(request.getMessageType()) {
            case TEXT:
                if (request.getText().trim().isEmpty()) {
                    throw new ChatMessageException(ErrorStatus.EMPTY_MESSAGE_TEXT);
                }
                newChatMessage = ChatMessageConverter.toChatMessage(request, chatRoom);
                break;

            case IMAGE:
                // 이미지 파일 업로드 처리
                if (request.getImageFiles().isEmpty()) {
                    throw new ChatMessageException(ErrorStatus.EMPTY_MESSAGE_IMAGE);
                }
                List<String> imageUrls = new ArrayList<>();
                List<String> uuids = new ArrayList<>();
                for (MultipartFile imageFile : request.getImageFiles()) {
                    String uuid = uuidService.generateUniqueUuidString();
                    String imageUrl = s3Manager.uploadFile(s3Manager.generateChatMessageImageKeyName(uuid), request.getFile());
                    uuids.add(uuid);
                    imageUrls.add(imageUrl);
                }
                newChatMessage = ChatMessageConverter.toChatMessageWithImages(request, chatRoom, imageUrls, uuids);
                break;

            case FILE:
                // 이미지 외 일반 파일 업로드 처리
                if (request.getFile().isEmpty()) {
                    throw new ChatMessageException(ErrorStatus.EMPTY_MESSAGE_FILE);
                }
                String uuid = uuidService.generateUniqueUuidString();
                String fileUrl = s3Manager.uploadFile(s3Manager.generateChatMessageFileKeyName(uuid), request.getFile());
                newChatMessage = ChatMessageConverter.toChatMessageWithFile(request, chatRoom, fileUrl, uuid);
                break;

            case LINK:
                // 링크 처리 로직
                if (request.getText().trim().isEmpty()) {
                    throw new ChatMessageException(ErrorStatus.EMPTY_MESSAGE_TEXT);
                }
                newChatMessage = ChatMessageConverter.toChatMessage(request, chatRoom);
                break;

            default:
                throw new ChatMessageException(ErrorStatus.INVALID_MESSAGE_TYPE);
        }

        // 3. 메세지를 MongoDB에 저장한다.
        return chatMessageRepository.save(newChatMessage);
    }
}
