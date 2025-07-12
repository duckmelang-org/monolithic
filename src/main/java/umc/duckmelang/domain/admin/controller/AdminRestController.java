package umc.duckmelang.domain.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import umc.duckmelang.domain.admin.converter.AdminConverter;
import umc.duckmelang.domain.admin.dto.AdminResponseDto;
import umc.duckmelang.domain.admin.service.AdminCommandService;
import umc.duckmelang.domain.admin.service.AdminQueryService;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.domain.member.converter.MemberProfileConverter;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.dto.mypage.MyPageRequestDto;
import umc.duckmelang.domain.member.dto.mypage.MyPageResponseDto;
import umc.duckmelang.domain.member.service.mypage.MyPageCommandService;
import umc.duckmelang.domain.post.service.post.PostCommandService;
import umc.duckmelang.global.apipayload.ApiResponse;
import umc.duckmelang.global.validation.annotation.ExistPost;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name ="Admin", description = "관리자 API")
@Validated
public class AdminRestController {
    private final AdminCommandService adminCommandService;
    private final AdminQueryService adminQueryService;
    private final PostCommandService postCommandService;
    private final MyPageCommandService myPageCommandService;

    @PatchMapping("/managers/join/{loginId}")
    @Operation(summary = "타 계정 관리자 추가 API", description = "관리자 권한을 부여할 member의 아이디(loginId)를 입력해주세요")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AdminResponseDto.AdminManagerDto> joinAdmin(@PathVariable(name="loginId") String loginId) {
        Member member = adminCommandService.joinAdmin(loginId);
        return ApiResponse.onSuccess(AdminConverter.adminManagerDto(member));
    }

    @PatchMapping("/managers/delete/{loginId}")
    @Operation(summary = "타 계정 관리자 삭제 API", description = "관리자 권한을 삭제할 member의 userId를 입력해주세요")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AdminResponseDto.AdminManagerDto> deleteAdmin(@PathVariable(name="loginId") Long userId) {
        Member member = adminCommandService.deleteAdmin(userId);
        return ApiResponse.onSuccess(AdminConverter.adminManagerDto(member));
    }

    @GetMapping("/managers")
    @Operation(summary = "관리자 목록 조회 API", description = "현재 관리자 목록을 조회힙니다")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<AdminResponseDto.AdminManagerDto>> getAdminList() {
        List<Member> members = adminQueryService.getAdmin();
        List<AdminResponseDto.AdminManagerDto> adminListDto = AdminConverter.toAdminManagerList(members);
        return ApiResponse.onSuccess(adminListDto);
    }

    @DeleteMapping("/members/{memberId}")
    @Operation(summary = "타 계정 삭제 API", description = "관리자가 타 사용자 계정을 삭제시킵니다")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteOtherMember(@PathVariable(name="memberId") Long memberId) {
        adminCommandService.deleteOtherMember(memberId);
        return ApiResponse.onSuccess("성공적으로 삭제되었습니다.");
    }

    @DeleteMapping("/posts/{postId}")
    @Operation(summary = "타 계정 게시글 삭제 API", description = "관리자가 타 사용자의 게시글을 삭제시킵니다.")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String>deletePost(@ExistPost @PathVariable(name="postId") Long postId){
        postCommandService.deleteMyPost(postId);
        return ApiResponse.onSuccess("게시글을 성공적으로 삭제했습니다.");
    }

    @PatchMapping("/members/profile/{memberId}")
    @Operation(summary = "타 계정 닉네임, 자기소개 수정 API", description = "관리자가 타 사용자의 닉네임과 자기소개를 수정합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<MyPageResponseDto.MyPageProfileEditAfterDto> updateMyPageMemberProfile(@PathVariable(name="memberId")Long memberId, @RequestBody MyPageRequestDto.UpdateMemberProfileDto request) {
        Member updatedMember = myPageCommandService.updateMemberProfile(memberId, request);
        return ApiResponse.onSuccess(MemberProfileConverter.toMemberProfileEditAfterDto(updatedMember));
    }


}
