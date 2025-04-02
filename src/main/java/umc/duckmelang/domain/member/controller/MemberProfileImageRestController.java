package umc.duckmelang.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.duckmelang.domain.member.converter.MemberProfileImageConverter;
import umc.duckmelang.domain.member.domain.MemberProfileImage;
import umc.duckmelang.domain.member.dto.profileImage.MemberProfileImageRequestDto;
import umc.duckmelang.domain.member.dto.profileImage.MemberProfileImageResponseDto;
import umc.duckmelang.domain.member.service.profileImage.MemberProfileImageCommandService;
import umc.duckmelang.domain.member.service.profileImage.MemberProfileImageQueryService;
import umc.duckmelang.global.apipayload.ApiResponse;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.global.validation.annotation.ValidPageNumber;

@RestController
@Tag(name="Images", description = "내 프로필 사진 관련 API")
@RequestMapping("/mypage/profile/image")
@RequiredArgsConstructor
public class MemberProfileImageRestController{
    private final MemberProfileImageQueryService memberProfileImageQueryService;
    private final MemberProfileImageCommandService memberProfileImageCommandService;

    @Operation(summary = "내 프로필 사진 전체 조회 API", description = "본인의 프로필 사진을 모두 조회하는 API입니다. 비공개된 사진과 공개된 사진 모두 확인 가능합니다.")
    @GetMapping("")
    public ApiResponse<MemberProfileImageResponseDto.MemberProfileImageListDto> getAllProfileImages(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                                    @ValidPageNumber @RequestParam(name = "page",  defaultValue = "0") Integer page) {
        Page<MemberProfileImage> memberProfileImagePage = memberProfileImageQueryService.getAllMemberProfileImageByMemberId(userDetails.getMemberId(), page);
        return ApiResponse.onSuccess(MemberProfileImageConverter.toMemberProfileImageListDto(memberProfileImagePage));
    }

    @Operation(summary = "내 프로필 사진 삭제 API", description = "본인의 프로필 사진 중 하나를 삭제하는 API입니다.")
    @DeleteMapping("/{imageId}")
    public ApiResponse<MemberProfileImageResponseDto.DeleteProfileImageResultDto> deleteProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("imageId") Long imageId) {
        memberProfileImageCommandService.deleteProfileImage(userDetails.getMemberId(), imageId);
        return ApiResponse.onSuccess(MemberProfileImageConverter.toDeleteProfileImageResultDto());
    }

    @Operation(summary = "내 프로필 사진 공개 범위 전환 API", description = "특정 회원 본인의 프로필 사진 중 하나를 공개로 전환하거나 비공개로 전환하는 API입니다. " +
            "\n false를 입력하여 PRIVATE 으로 변경하고 true를 입력하여 PUBLIC 으로 변환가능합니다.")
    @PatchMapping("/{imageId}/status")
    public ApiResponse<MemberProfileImageResponseDto.UpdateProfileImageStatusResultDto> updateProfileImageStatus(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                                                 @PathVariable("imageId") Long imageId,
                                                                                                                 @RequestBody @Valid MemberProfileImageRequestDto.UpdateProfileImageStatusDto request) {
        MemberProfileImage updatedMemberProfileImage = memberProfileImageCommandService.updateProfileImageStatus(userDetails.getMemberId(), imageId, request);
        return ApiResponse.onSuccess(MemberProfileImageConverter.toUpdateProfileImageStatusResultDto(updatedMemberProfileImage));
    }
}
