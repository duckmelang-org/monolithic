package umc.duckmelang.domain.chatroom.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.application.domain.Application;
import umc.duckmelang.domain.application.repository.ApplicationRepository;
import umc.duckmelang.domain.chatroom.domain.ChatRoom;
import umc.duckmelang.domain.chatroom.dto.ChatRoomResponseDto;
import umc.duckmelang.domain.chatroom.repository.ChatRoomRepository;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.memberprofileimage.domain.MemberProfileImage;
import umc.duckmelang.domain.memberprofileimage.repository.MemberProfileImageRepository;
import umc.duckmelang.domain.memberprofileimage.service.MemberProfileImageQueryService;
import umc.duckmelang.domain.postimage.domain.PostImage;
import umc.duckmelang.domain.postimage.repository.PostImageRepository;
import umc.duckmelang.domain.postimage.service.PostImageQueryService;
import umc.duckmelang.domain.review.domain.Review;
import umc.duckmelang.domain.review.repository.ReviewRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.ChatRoomException;
import umc.duckmelang.global.apipayload.exception.MemberException;
import umc.duckmelang.mongo.chatmessage.dto.ChatMessageResponseDto;
import umc.duckmelang.mongo.chatmessage.service.ChatMessageQueryService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomQueryServiceImpl implements ChatRoomQueryService {
    private final ChatRoomRepository chatRoomRepository;
    private final PostImageRepository postImageRepository;
    private final MemberProfileImageRepository memberProfileImageRepository;
    private final ChatMessageQueryService chatMessageService;
    private final PostImageQueryService postImageQueryService;
    private final MemberProfileImageQueryService memberProfileImageQueryService;
    private final ApplicationRepository applicationRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public Page<ChatRoomResponseDto.ChatRoomItemDto> findAllChatRooms(Long memberId, int page) {
        Pageable pageable = PageRequest.of(page,20);
        Page<ChatRoom> chatRooms = chatRoomRepository.findAllByMemberWithPostAndCounterpart(memberId, pageable);

        List<ChatRoomResponseDto.ChatRoomItemDto> chatRoomItemDtos = getChatRoomItemDtoList(chatRooms, memberId);

        return new PageImpl<>(chatRoomItemDtos, pageable, chatRooms.getTotalElements());
    }

    @Override
    public Page<ChatRoomResponseDto.ChatRoomItemDto> findOngoingChatRooms(Long memberId, int page) {
        Pageable pageable = PageRequest.of(page,20);
        Page<ChatRoom> chatRooms = chatRoomRepository.findOngoingByMemberId(memberId, pageable);

        List<ChatRoomResponseDto.ChatRoomItemDto> chatRoomItemDtos = getChatRoomItemDtoList(chatRooms, memberId);

        return new PageImpl<>(chatRoomItemDtos, pageable, chatRooms.getTotalElements());
    }

    @Override
    public Page<ChatRoomResponseDto.ChatRoomItemDto> findConfirmedChatRooms(Long memberId, int page) {
        Pageable pageable = PageRequest.of(page,20);
        Page<ChatRoom> chatRooms = chatRoomRepository.findConfirmedByMemberId(memberId, pageable);
        List<ChatRoomResponseDto.ChatRoomItemDto> chatRoomItemDtos = getChatRoomItemDtoList(chatRooms, memberId);

        return new PageImpl<>(chatRoomItemDtos, pageable, chatRooms.getTotalElements());
    }

    @Override
    public Page<ChatRoomResponseDto.ChatRoomItemDto> findTerminatedChatRooms(Long memberId, int page) {
        Pageable pageable = PageRequest.of(page,20);
        Page<ChatRoom> chatRooms = chatRoomRepository.findTerminatedByMemberId(memberId, pageable);
        List<ChatRoomResponseDto.ChatRoomItemDto> chatRoomItemDtos = getChatRoomItemDtoList(chatRooms, memberId);

        return new PageImpl<>(chatRoomItemDtos, pageable, chatRooms.getTotalElements());
    }

    @Override
    public ChatRoomResponseDto.ChatRoomDetailDto findChatRoomDetail(Long memberId, Long chatRoomId) {
        //chatroom, member, post, application, review 엔티티 조회
        ChatRoom chatRoom = chatRoomRepository.findChatRoomWithPostAndMember(chatRoomId).orElseThrow(()->new ChatRoomException(ErrorStatus.CHATROOM_NOT_FOUND));
        Application application = applicationRepository.findByPostIdAndMemberId(chatRoom.getPost().getId(), chatRoom.getOtherMember().getId()).orElse(null);
        Review review = null;
        if (application != null)
            review = reviewRepository.findSentReviewToSpecificApplication(memberId, application.getId()).orElse(null);

        Member member = null;
        if (Objects.equals(chatRoom.getPost().getMember().getId(), memberId))
            member = chatRoom.getOtherMember();
        else if (Objects.equals(chatRoom.getOtherMember().getId(), memberId))
            member = chatRoom.getPost().getMember();
        else throw new MemberException(ErrorStatus.UNAUTHORIZED_MEMBER);

        MemberProfileImage memberProfileImage = memberProfileImageRepository.findFirstByMemberIdAndIsPublicTrueOrderByCreatedAtDesc(member.getId()).orElse(null);
        PostImage postImage = postImageRepository.findFirstByPostIdOrderByCreatedAtAsc(chatRoom.getPost().getId()).orElse(null);

        return ChatRoomResponseDto.ChatRoomDetailDto.builder()
                .myId(memberId)
                .oppositeId(member.getId())
                .oppositeNickname(member.getNickname())
                .oppositeProfileImage(memberProfileImage != null ? memberProfileImage.getMemberImage() : "")
                .postId(chatRoom.getPost().getId())
                .postTitle(chatRoom.getPost().getTitle())
                .postImage(postImage != null ? postImage.getPostImageUrl() : "")
                .isPostOwner(member==chatRoom.getPost().getMember())
                .chatRoomStatus(chatRoom.getChatRoomStatus())
                .applicationStatus(application!=null ? application.getStatus() : null)
                .applicationId(application != null ? application.getId() : -1)
                .reviewId(review != null ? review.getId() : -1)
                .build();
    }

    public List<ChatRoomResponseDto.ChatRoomItemDto> getChatRoomItemDtoList(Page<ChatRoom> chatRooms, Long memberId){

        // 채팅방 ID 리스트 추출
        List<Long> chatRoomIds = chatRooms.stream()
                .map(ChatRoom::getId)
                .collect(Collectors.toList());

        List<Long> postIds = chatRooms.stream()
                .map(chatRoom -> chatRoom.getPost().getId())
                .collect(Collectors.toList());

        List<Long> memberIds = chatRooms.stream()
                .map(chatRoom -> {
                    // 현재 회원이 게시글 작성자인지 확인
                    boolean isPostWriter = chatRoom.getPost().getMember().getId().equals(memberId);

                    // 상대방 정보 설정
                    Member otherMember = isPostWriter ? chatRoom.getOtherMember() : chatRoom.getPost().getMember();
                    return otherMember.getId();})
                .collect(Collectors.toList());

        // 한 번의 쿼리로 모든 채팅방의 최신 메시지 조회
        Map<Long, ChatMessageResponseDto.LatestChatMessageDto> latestMessagesMap =
                chatMessageService.getLatestMessagesByChatRoomIds(chatRoomIds);

        //한 번의 쿼리로 모든 post의 postImage 조회
        Map<Long, String> postImages = postImageQueryService.getFirstImageUrlsForPosts(postIds);

        //한 번의 쿼리로 모든 member의 memberProfileImage 조회
        Map<Long, String> memberProfileImages = memberProfileImageQueryService.getFirstProfileImageUrlsForMembers(memberIds);

        return chatRooms
                .stream().sorted((chatRoom1, chatRoom2) -> {
                    LocalDateTime priority1 = Optional.ofNullable(latestMessagesMap.get(chatRoom1.getId()))
                            .map(ChatMessageResponseDto.LatestChatMessageDto::getCreatedAt)
                            .orElse(LocalDateTime.MAX);
                    LocalDateTime priority2 = Optional.ofNullable(latestMessagesMap.get(chatRoom2.getId()))
                            .map(ChatMessageResponseDto.LatestChatMessageDto::getCreatedAt)
                            .orElse(LocalDateTime.MAX);
                    return priority1.compareTo(priority2);
                })
                .map(chatRoom -> {
                    // 현재 회원이 게시글 작성자인지 확인
                    boolean isPostWriter = chatRoom.getPost().getMember().getId().equals(memberId);

                    // 상대방 정보 설정
                    Member otherMember = isPostWriter ? chatRoom.getOtherMember() : chatRoom.getPost().getMember();

                    // 게시글의 최신 이미지 조회
                    String latestPostImageUrl = postImages.get(chatRoom.getPost().getId());

                    // 상대방의 최신 프로필 이미지 조회
                    String latestProfileImageUrl = memberProfileImages.get(otherMember.getId());

                    //post eventdate 지났는지 확인하고 chatroom status 변경
                    chatRoom.hasTerminated();

                    // Map에서 해당 채팅방의 최신 메시지 조회
                    ChatMessageResponseDto.LatestChatMessageDto latestMessage = latestMessagesMap.get(chatRoom.getId());

                    return ChatRoomResponseDto.ChatRoomItemDto.builder()
                            .chatRoomId(chatRoom.getId())
                            .postId(chatRoom.getPost().getId())
                            .postTitle(chatRoom.getPost().getTitle())
                            .postImage(latestPostImageUrl)
                            .oppositeId(otherMember.getId())
                            .oppositeNickname(otherMember.getNickname())
                            .oppositeProfileImage(latestProfileImageUrl)
                            .status(chatRoom.getChatRoomStatus().toString())
                            .lastMessage(latestMessage != null ? latestMessage.getContent() : null)
                            .lastMessageTime(latestMessage != null ? latestMessage.getCreatedAt() : null)
                            .build();
                })
                .collect(Collectors.toList());

    }

    @Override
    public Integer getChatRoomCount(Long postId){
        return chatRoomRepository.countByPostId(postId);
    }

}
