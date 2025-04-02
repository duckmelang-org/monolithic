package umc.duckmelang.domain.review.service;

import umc.duckmelang.domain.review.domain.Review;
import umc.duckmelang.domain.review.dto.ReviewRequestDto;

public interface ReviewCommandService {
    Review joinReview(ReviewRequestDto.ReviewJoinDto request , Long memberId);
    void deleteReview(Long reviewId);
}
