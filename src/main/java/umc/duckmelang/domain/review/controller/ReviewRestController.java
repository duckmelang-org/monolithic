package umc.duckmelang.domain.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import umc.duckmelang.domain.application.domain.Application;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.review.converter.ReviewConverter;
import umc.duckmelang.domain.review.domain.Review;
import umc.duckmelang.domain.review.dto.ReviewRequestDto;
import umc.duckmelang.domain.review.dto.ReviewResponseDto;
import umc.duckmelang.domain.review.service.ReviewCommandService;
import umc.duckmelang.domain.review.service.ReviewQueryService;
import umc.duckmelang.global.annotations.CommonApiResponses;
import umc.duckmelang.global.apipayload.ApiResponse;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.ApplicationException;
import umc.duckmelang.global.apipayload.exception.MemberException;
import umc.duckmelang.global.security.user.CustomUserDetails;
import umc.duckmelang.global.validation.annotation.ExistPost;
import umc.duckmelang.global.validation.annotation.ExistsMember;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@Validated
public class ReviewRestController {
    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;
    private final MemberRepository memberRepository;

    @PostMapping("")
    @CommonApiResponses
    @Operation(summary = "후기글 작성 API", description = "후기글 작성 API입니다. applicationId는 후기글 작성 페이지 내 관련 정보 조회 API에서 얻어오면 됩니다")
    public ApiResponse<ReviewResponseDto.ReviewJoinResultDto> joinReview (@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid ReviewRequestDto.ReviewJoinDto request){
        Long memberId = userDetails.getMemberId();
        Review review = reviewCommandService.joinReview(request, memberId);
        return ApiResponse.onSuccess(ReviewConverter.reviewJoinResultDto(review));
    }

    @DeleteMapping("/{reviewId}")
    @CommonApiResponses
    @Operation(summary="후기글 삭제 API", description = "후기글 삭제 API입니다")
    public ApiResponse<String> deleteReview (@PathVariable Long reviewId){
        reviewCommandService.deleteReview(reviewId);
        return ApiResponse.onSuccess("후기를 성공적으로 삭제했습니다");

    }

    @GetMapping("/information")
    @CommonApiResponses
    @Operation(summary = "후기글 작성 페이지 내 관련 정보 조회 API", description = "후기글 작성 페이지에서 applicationId 외에 유저네임, 게시글 제목, 행사 날짜 등 정보를 보여주는 API 입니다. 최신 순으로 정렬되어 list로 내보냅니다.")
    public ApiResponse<List<ReviewResponseDto.ReviewInformationDto>> getReviewInformation(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam Long memberId){
        Long myId = userDetails.getMemberId();
        List<Application> applications = reviewQueryService.getReviewInformation(myId, memberId);

        if (applications.isEmpty()) {
            throw new ApplicationException(ErrorStatus.APPLICATION_NOT_FOUND);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

        List<ReviewResponseDto.ReviewInformationDto> reviewInformationDtos = applications.stream()
                .map(application -> ReviewConverter.reviewInformationDto(application, member ))
                .collect(Collectors.toList());

        return ApiResponse.onSuccess(reviewInformationDtos);
    }

    @GetMapping("/chat/information")
    @CommonApiResponses
    @Operation(summary = "채팅창 - 후기글 작성 페이지 내 관련 정보 조회 API", description = "채팅창 내에서 후기를 작성할 때, postId와 상대의 id를 넣습니다. 항상 1개의 값만 나옵니다")
    public ApiResponse<ReviewResponseDto.ReviewInformationDto> getReviewInformationByPost(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam Long memberId, @RequestParam Long postId){
        Long myId = userDetails.getMemberId();
        Application application = reviewQueryService.getReviewInformationByPost(myId, memberId, postId)
                .orElseThrow(() -> new ApplicationException(ErrorStatus.APPLICATION_NOT_FOUND));


        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));


        return ApiResponse.onSuccess(ReviewConverter.reviewInformationDto(application, member));
    }
}
