package umc.duckmelang.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import umc.duckmelang.domain.member.dto.MyPageResponseDto;
import umc.duckmelang.domain.member.facade.ProfileFacadeService;
import umc.duckmelang.global.validation.annotation.ExistsMember;
import umc.duckmelang.domain.memberprofileimage.converter.MemberProfileImageConverter;
import umc.duckmelang.domain.memberprofileimage.domain.MemberProfileImage;
import umc.duckmelang.domain.memberprofileimage.dto.MemberProfileImageResponseDto;
import umc.duckmelang.domain.memberprofileimage.service.MemberProfileImageQueryService;
import umc.duckmelang.domain.post.converter.PostConverter;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.dto.PostResponseDto;
import umc.duckmelang.domain.post.service.PostQueryService;
import umc.duckmelang.global.validation.annotation.ValidPageNumber;
import umc.duckmelang.domain.review.converter.ReviewConverter;
import umc.duckmelang.domain.review.domain.Review;
import umc.duckmelang.domain.review.dto.ReviewResponseDto;
import umc.duckmelang.domain.review.service.ReviewQueryService;
import umc.duckmelang.global.annotations.CommonApiResponses;
import umc.duckmelang.global.apipayload.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/profile")
public class ProfileRestController {
    private final ProfileFacadeService profileFacadeService;
    private final MemberProfileImageQueryService memberProfileImageQueryService;
    private final PostQueryService postQueryService;
    private final ReviewQueryService reviewQueryService;

    @Operation(summary = "다른 멤버 프로필 조회 API",description = "path variable로 프로필을 조회하고자하는 상대 member의 id를 받습니다.")
    @GetMapping(path = "/{memberId}")
    ApiResponse<MyPageResponseDto.MyPageProfileDto> getOtherProfile(@PathVariable @ExistsMember Long memberId) {
        return ApiResponse.onSuccess(profileFacadeService.getProfileByMemberId(memberId));
    }

    @Operation(summary = "다른 멤버 프로필 사진 조회 API",description = "path variable로 프로필 사진들을 조회하고자하는 상대 member의 id를 받습니다. 디폴트 사진을 제외한 사진 리스트를 보냅니다.")
    @GetMapping(path = "/{memberId}/images")
    ApiResponse<MemberProfileImageResponseDto.MemberProfileImageListDto> getProfileImages(@PathVariable @ExistsMember Long memberId, @ValidPageNumber @RequestParam(name = "page",  defaultValue = "0") Integer page) {
        Page<MemberProfileImage> memberProfileImagePage = memberProfileImageQueryService.getPublicMemberProfileImageByMemberId(memberId, page);
        return ApiResponse.onSuccess(MemberProfileImageConverter.toMemberProfileImageListDto(memberProfileImagePage));
    }

    @Operation(summary = "다른 멤버가 업로드한 게시글들 조회 API",description = "path variable로 게시글 조회하고자하는 상대 member의 id를 받습니다.")
    @GetMapping(path = "/{memberId}/posts")
    ApiResponse<PostResponseDto.PostPreviewListDto> getOtherPostList(@PathVariable @ExistsMember Long memberId, @ValidPageNumber @RequestParam(name = "page",  defaultValue = "0") Integer page) {
        Page<Post> postList = postQueryService.getPostListByMember(memberId, page);
        return ApiResponse.onSuccess(PostConverter.postPreviewListDto(postList));
    }

    @Operation(summary = "다른 사람의 동행 후기 조회 API", description = "path variable로 동행 후기를 조회하고자 하는 상대 member의 id를 받습니다.")
    @GetMapping("/{memberId}/reviews")
    @CommonApiResponses
    public ApiResponse<ReviewResponseDto.ReviewListDto> getOtherReviewList(@PathVariable @ExistsMember Long memberId){
        List<Review> reviewList = reviewQueryService.getReviewList(memberId);
        double averageScore = reviewQueryService.calculateAverageScore(reviewList);
        return ApiResponse.onSuccess(ReviewConverter.reviewListDto(reviewList, averageScore));
    }
}
