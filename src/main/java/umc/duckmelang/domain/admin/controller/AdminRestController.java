package umc.duckmelang.domain.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.duckmelang.domain.admin.converter.AdminConverter;
import umc.duckmelang.domain.admin.dto.AdminResponseDto;
import umc.duckmelang.domain.admin.service.AdminCommandService;
import umc.duckmelang.domain.admin.service.AdminQueryService;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.domain.idolcategory.dto.IdolCategoryRequestDto;
import umc.duckmelang.domain.idolcategory.service.IdolCategoryCommandService;
import umc.duckmelang.domain.member.converter.MemberProfileConverter;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.enums.Role;
import umc.duckmelang.domain.member.dto.mypage.MyPageRequestDto;
import umc.duckmelang.domain.member.dto.mypage.MyPageResponseDto;
import umc.duckmelang.domain.member.service.mypage.MyPageCommandService;
import umc.duckmelang.domain.post.service.post.PostCommandService;
import umc.duckmelang.global.apipayload.ApiResponse;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.MemberException;
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
    private final IdolCategoryCommandService idolCategoryCommandService;

    @PatchMapping("/managers/join/{loginId}")
    @Operation(summary = "타 계정 관리자 추가 API", description = "관리자 권한을 부여할 member의 아이디(loginId)를 입력해주세요")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AdminResponseDto.AdminManagerDto> joinAdmin(@PathVariable(name="loginId") String loginId) {
        Member member = adminCommandService.joinAdmin(loginId);
        return ApiResponse.onSuccess(AdminConverter.adminManagerDto(member));
    }

    @PatchMapping("/managers/delete/{memberId}")
    @Operation(summary = "타 계정 관리자 삭제 API", description = "관리자 권한을 삭제할 member의 memberId를 입력해주세요")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AdminResponseDto.AdminManagerDto> deleteAdmin(@PathVariable(name="memberId") Long memberId) {
        Member member = adminCommandService.deleteAdmin(memberId);
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

    @DeleteMapping("/admin/idols/{idolId}")
    @Operation(summary = "특정 아이돌 카테고리 삭제 API(admin)", description = "관리자 기능. 아이돌 카테고리에서 특정 아이돌을 삭제합니다.")
    public ApiResponse<String> deleteIdol(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("idolId") Long idolId){
        if(userDetails.getRole() != Role.ADMIN)
            throw new MemberException(ErrorStatus._FORBIDDEN);
        idolCategoryCommandService.deleteIdolCategory(idolId);
        return ApiResponse.onSuccess("해당 아이돌 카테고리를 삭제했습니다.");
    }

    @PostMapping(value = "/admin/idols", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "아이돌 카테고리 추가 API(admin)", description = "관리자 기능. 아이돌 카테고리에 아이돌 이름, 이미지를 추가합니다.")
    public ApiResponse<String> createIdol(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("names") List<String> names,
                                          @RequestPart("files") List<MultipartFile> files) {
        if(userDetails.getRole() != Role.ADMIN)
            throw new MemberException(ErrorStatus._FORBIDDEN);
        idolCategoryCommandService.createIdolCategory(names, files);
        return ApiResponse.onSuccess("아이돌 카테고리를 추가했습니다.");
    }

    @PostMapping(value = "/admin/idols/update", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "아이돌 카테고리 수정 API(admin)", description = "관리자 기능. 기존 아이돌 카테고리의 이름, 이미지를 수정합니다.")
    public ApiResponse<String> updateIdol(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestPart("dtos") IdolCategoryRequestDto.IdolCategoryRequestList request, @RequestPart("files") List<MultipartFile> files){
        if(userDetails.getRole() != Role.ADMIN)
            throw new MemberException(ErrorStatus._FORBIDDEN);
        idolCategoryCommandService.updateIdolCategory(request, files);
        return ApiResponse.onSuccess("아이돌 카테고리를 수정했습니다.");
    }
}
