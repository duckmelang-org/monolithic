package umc.duckmelang.domain.post.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.bookmark.service.BookmarkQueryService;
import umc.duckmelang.domain.chatroom.service.ChatRoomQueryService;
import umc.duckmelang.domain.post.converter.PostConverter;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.dto.PostResponseDto;
import umc.duckmelang.domain.post.service.post.PostQueryService;
import umc.duckmelang.domain.review.domain.Review;
import umc.duckmelang.domain.review.service.ReviewQueryService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostFacadeService {
    private final PostQueryService postQueryService;
    private final ReviewQueryService reviewQueryService;
    private final BookmarkQueryService bookmarkQueryService;
    private final ChatRoomQueryService chatRoomQueryService;

    public PostResponseDto.PostDetailDto getPostDetail(Long postId) {
        Post post = postQueryService.getPostDetail(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다"));

        List<Review> reviewList = Optional.ofNullable(reviewQueryService.getReviewList(post.getMember().getId()))
                .orElse(Collections.emptyList());

        double averageScore = reviewQueryService.calculateAverageScore(reviewList);
        Integer bookmarkCount = bookmarkQueryService.getBookmarkCount(postId);
        Integer chatCount = chatRoomQueryService.getChatRoomCount(postId);

        return PostConverter.postDetailDto(post, averageScore, bookmarkCount, chatCount);
    }

}
