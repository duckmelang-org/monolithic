package umc.duckmelang.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.application.domain.Application;
import umc.duckmelang.domain.application.repository.ApplicationRepository;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.notification.service.NotificationHelper;
import umc.duckmelang.domain.review.converter.ReviewConverter;
import umc.duckmelang.domain.review.domain.Review;
import umc.duckmelang.domain.review.dto.ReviewRequestDto;
import umc.duckmelang.domain.review.repository.ReviewRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.ApplicationException;
import umc.duckmelang.global.apipayload.exception.MemberException;

import static umc.duckmelang.domain.notification.domain.enums.NotificationType.REVIEW;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewCommandServiceImpl implements ReviewCommandService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ApplicationRepository applicationRepository;
    private final NotificationHelper notificationHelper;

    @Override
    public Review joinReview(ReviewRequestDto.ReviewJoinDto request , Long memberId){
        Member sender = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
        Member receiver = memberRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ApplicationException(ErrorStatus.APPLICATION_NOT_FOUND));

        Review review = ReviewConverter.toReview(request, sender, receiver,application);

        notificationHelper.sendNotification(sender.getId(), receiver.getId(), REVIEW, sender.getNickname() + " 님이 후기를 작성했어요");

        return reviewRepository.save(review);
    }
}
