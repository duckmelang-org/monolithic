package umc.duckmelang.domain.post.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.bookmark.service.BookmarkQueryService;
import umc.duckmelang.domain.chatroom.domain.ChatRoom;
import umc.duckmelang.domain.chatroom.repository.ChatRoomRepository;
import umc.duckmelang.domain.chatroom.service.ChatRoomQueryService;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.post.converter.PostConverter;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.dto.PostResponseDto;
import umc.duckmelang.domain.post.service.post.PostQueryService;
import umc.duckmelang.domain.review.domain.Review;
import umc.duckmelang.domain.review.service.ReviewQueryService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostFacadeService {
    private final PostQueryService postQueryService;
    private final ReviewQueryService reviewQueryService;
    private final BookmarkQueryService bookmarkQueryService;
    private final ChatRoomQueryService chatRoomQueryService;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    public PostResponseDto.PostDetailDto getPostDetail(Long postId, Long memberId) {
        Post post = postQueryService.getPostDetail(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다"));

        List<Review> reviewList = Optional.ofNullable(reviewQueryService.getReviewList(post.getMember().getId()))
                .orElse(Collections.emptyList());

        double averageScore = reviewQueryService.calculateAverageScore(reviewList);
        Integer bookmarkCount = bookmarkQueryService.getBookmarkCount(postId);
        Integer chatCount = chatRoomQueryService.getChatRoomCount(postId);

        Member member = memberRepository.findById(memberId).orElse(null);

        ChatRoom chatRoom = null;
        if (member != null && !Objects.equals(post.getMember().getId(), member.getId())) {
            chatRoom = chatRoomRepository.findByPostIdAndOtherMemberId(post.getId(), member.getId()).orElse(null);
        }

        Long chatRoomId = chatRoom != null ? chatRoom.getId() : null;

        return PostConverter.postDetailDto(post, averageScore, bookmarkCount, chatCount, chatRoomId);
    }

}
