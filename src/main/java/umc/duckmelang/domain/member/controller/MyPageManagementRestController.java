package umc.duckmelang.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.duckmelang.domain.member.converter.MemberProfileConverter;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.dto.mypage.MyPageRequestDto;
import umc.duckmelang.domain.member.dto.mypage.MyPageResponseDto;
import umc.duckmelang.domain.member.service.mypage.MyPageCommandService;
import umc.duckmelang.domain.member.service.mypage.MyPageQueryService;
import umc.duckmelang.domain.memberprofileimage.converter.MemberProfileImageConverter;
import umc.duckmelang.domain.memberprofileimage.domain.MemberProfileImage;
import umc.duckmelang.domain.memberprofileimage.dto.MemberProfileImageResponseDto;
import umc.duckmelang.domain.memberprofileimage.service.MemberProfileImageCommandService;
import umc.duckmelang.domain.post.service.PostCommandService;
import umc.duckmelang.global.apipayload.ApiResponse;
import umc.duckmelang.global.security.jwt.JwtUtil;
import umc.duckmelang.global.security.user.CustomUserDetails;
import umc.duckmelang.global.validation.annotation.ExistPost;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Validated
public class MyPageManagementRestController {
    private final MyPageCommandService myPageCommandService;
    private final MemberProfileImageCommandService memberProfileImageCommandService;
    private final MyPageQueryService myPageQueryService;
    private final PostCommandService postCommandService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "내 프로필 수정 - 기존 프로필 정보 조회 API", description = "피그마 상에서는 기존 프로필 사진만 조회하게 되어있는데 혹시 닉네임도 필요하실까 해서 일단 넣어놓았습니다.")
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
    public ApiResponse<MemberProfileImageResponseDto.MemberProfileImageDto> updateProfileImage (@AuthenticationPrincipal CustomUserDetails userDetails, @RequestPart("profileImage")MultipartFile profileImage) {
        MemberProfileImage memberProfileImage = memberProfileImageCommandService.createProfileImage(userDetails.getMemberId(), profileImage);
        return ApiResponse.onSuccess(MemberProfileImageConverter.toMemberProfileImageDto(memberProfileImage));
    }

    @Operation(summary = "피드 관리 - 피드 목록 삭제 API", description = "내 피드 목록을 삭제하는 API입니다.")
    @DeleteMapping("/posts/{postId}")
    public ApiResponse<String> deleteMyPost(@ExistPost @PathVariable("postId") Long postId){
        postCommandService.deleteMyPost(postId);
        return ApiResponse.onSuccess("피드를 성공적으로 삭제했습니다.");
    }

    @Operation(summary = "설정 - 로그인 정보 조회 API", description = "사용자의 로그인 정보를 반환합니다. 사용자 닉네임과 이메일 그리고 카카오톡/구글 이나 자체 로그인 사용자인지 구분합니다.")
    @GetMapping("/info")
    public ApiResponse<MyPageResponseDto.LoginInfoDto> getLoginInfo(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ApiResponse.onSuccess(myPageQueryService.getLoginInfo(userDetails.getMemberId()));
    }

    @Operation(summary = "설정 - 회원 탈퇴 API", description = "회원 탈퇴를 처리합니다.")
    @DeleteMapping("/account/delete")
    public ApiResponse<String> deleteMember(@AuthenticationPrincipal CustomUserDetails userDetails){
        myPageCommandService.deleteMember(userDetails.getMemberId());
        return ApiResponse.onSuccess("성공적으로 탈퇴했습니다.");
    }
}
