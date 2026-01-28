package umc.duckmelang.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.duckmelang.domain.member.converter.MemberProfileConverter;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.dto.mypage.MyPageRequestDto;
import umc.duckmelang.domain.member.dto.mypage.MyPageResponseDto;
import umc.duckmelang.domain.member.facade.ProfileFacadeService;
import umc.duckmelang.domain.member.service.mypage.MyPageCommandService;
import umc.duckmelang.domain.member.service.mypage.MyPageQueryService;
import umc.duckmelang.domain.member.converter.MemberProfileImageConverter;
import umc.duckmelang.domain.member.domain.MemberProfileImage;
import umc.duckmelang.domain.member.dto.profileImage.MemberProfileImageResponseDto;
import umc.duckmelang.domain.member.service.profileImage.MemberProfileImageCommandService;
import umc.duckmelang.domain.post.converter.PostConverter;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.dto.PostResponseDto;
import umc.duckmelang.domain.post.service.post.PostCommandService;
import umc.duckmelang.domain.post.service.post.PostQueryService;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.domain.review.converter.ReviewConverter;
import umc.duckmelang.domain.review.domain.Review;
import umc.duckmelang.domain.review.dto.ReviewResponseDto;
import umc.duckmelang.domain.review.service.ReviewQueryService;
import umc.duckmelang.global.apipayload.annotations.CommonApiResponses;
import umc.duckmelang.global.apipayload.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/mypage")
@Tag(name="MyPage", description = "마이페이지에 해당하는 API")
@RequiredArgsConstructor
public class MypageController {
    private final ProfileFacadeService profileFacadeService;
    private final PostQueryService postQueryService;
    private final ReviewQueryService reviewQueryService;
    private final MyPageCommandService myPageCommandService;
    private final MemberProfileImageCommandService memberProfileImageCommandService;
    private final MyPageQueryService myPageQueryService;
    private final PostCommandService postCommandService;

    @Operation(summary = "마이페이지 - 조회 API", description = "마이페이지 첫 화면에 노출되는 회원 정보를 조회해오는 API입니다. 사용자의 닉네임, 성별, 나이, 대표 프로필 사진을 불러옵니다.")
    @GetMapping("")
    public ApiResponse<MyPageResponseDto.MyPagePreviewDto> getMyPageMemberPreview(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.onSuccess(profileFacadeService.getMyPageMemberPreview(userDetails.getMemberId()));
    }

    @Operation(summary = "마이페이지 - 내 프로필 조회 API", description = "마이페이지를 통해 접근할 수 있는 내 프로필을 조회해오는 API입니다. 사용자의 닉네임, 성별, 나이, 자기소개, 대표 프로필 사진, 특정 사용자가 작성한 게시글 수, 특정 사용자의 매칭 횟수를 불러옵니다.")
    @GetMapping("/profile")
    public ApiResponse<MyPageResponseDto.MyPageProfileDto> getMyPageMemberProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.onSuccess(profileFacadeService.getProfileByMemberId(userDetails.getMemberId()));
    }

    @GetMapping("/reviews")
    @CommonApiResponses
    @Operation(summary = "마이페이지 - 나와의 동행 후기 조회 API", description = "내 프로필에서 '나와의 동행 후기' 볼 때 이용하는 API 입니다.")
    public ApiResponse<ReviewResponseDto.ReviewListDto> getMyReviewList(@AuthenticationPrincipal CustomUserDetails userDetails){
        List<Review> reviewList = reviewQueryService.getReviewList(userDetails.getMemberId());
        double averageScore = reviewQueryService.calculateAverageScore(reviewList);
        return ApiResponse.onSuccess(ReviewConverter.reviewListDto(reviewList, averageScore));
    }

    @GetMapping("/posts")
    @Operation(summary = "마이페이지 - 내가 업로드한 게시글들 조회 API & 피드 관리 - 내 피드 목록 조회", description = "내 프로필에서 '업로드한 게시글 조회'와 피드 관리에서 '내 피드 목록' 조회에 사용되는 API입니다.")
    ApiResponse<PostResponseDto.PostPreviewListDto> getMyPostList(@AuthenticationPrincipal CustomUserDetails userDetails,  @RequestParam(name = "page",  defaultValue = "0") Integer page) {
        Page<Post> postList = postQueryService.getPostListByMember(userDetails.getMemberId(), page);
        return ApiResponse.onSuccess(PostConverter.postPreviewListDto(postList));
    }

    @Operation(summary = "내 프로필 수정 - 기존 프로필 정보 조회 API", description = "기존 프로필 사진, 기존 자기소개, 기존 닉네임을 반환합니다.")
    @GetMapping("/profile/edit")
    public ApiResponse<MyPageResponseDto.MyPageProfileEditBeforeDto> getMyPageMemberProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ApiResponse.onSuccess(myPageQueryService.getMemberProfileBeforeEdit(userDetails.getMemberId()));
    }

    @Operation(summary = "내 프로필 수정 - 닉네임, 자기소개 수정 API", description = "내 프로필을 수정하는 API입니다. 사용자의 닉네임과 자기소개를 수정합니다.")
    @PatchMapping("/profile/edit")
    public ApiResponse<MyPageResponseDto.MyPageProfileEditAfterDto> updateMyPageMemberProfile(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody MyPageRequestDto.UpdateMemberProfileDto request) {
        Member updatedMember = myPageCommandService.updateMemberProfile( userDetails.getMemberId(), request);
        return ApiResponse.onSuccess(MemberProfileConverter.toMemberProfileEditAfterDto(updatedMember));
    }

    @Operation(summary = "내 프로필 수정 - 내 프로필 사진 추가 API", description = "내 프로필을 수정하는 API입니다. 사용자의 프로필 이미지를 추가합니다.")
    @PostMapping(value = "/profile/image/edit", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<MemberProfileImageResponseDto.MemberProfileImageDto> updateProfileImage (@AuthenticationPrincipal CustomUserDetails userDetails, @RequestPart("profileImage") MultipartFile profileImage) {
        MemberProfileImage memberProfileImage = memberProfileImageCommandService.createProfileImage(userDetails.getMemberId(), profileImage);
        return ApiResponse.onSuccess(MemberProfileImageConverter.toMemberProfileImageDto(memberProfileImage));
    }

    @Operation(summary = "피드 관리 - 피드 목록 삭제 API", description = "내 피드 목록을 삭제하는 API입니다.")
    @DeleteMapping("/posts/{postId}")
    public ApiResponse<String> deleteMyPost( @PathVariable("postId") Long postId){
        postCommandService.deleteMyPost(postId);
        return ApiResponse.onSuccess("피드를 성공적으로 삭제했습니다.");
    }
}
