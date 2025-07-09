package umc.duckmelang.domain.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import umc.duckmelang.domain.admin.converter.AdminConverter;
import umc.duckmelang.domain.admin.dto.AdminResponseDto;
import umc.duckmelang.domain.admin.service.AdminCommandService;
import umc.duckmelang.domain.admin.service.AdminQueryService;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.global.apipayload.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name ="Admin", description = "관리자 API")
@Validated
public class AdminRestController {
    private final AdminCommandService adminCommandService;
    private final AdminQueryService adminQueryService;

    @PatchMapping("/managers/join/{loginId}")
    @Operation(summary = "타 계정 관리자 추가", description = "관리자 권한을 부여할 member의 아이디(loginId)를 입력해주세요")
    public ApiResponse<AdminResponseDto.AdminManagerDto> joinAdmin(@PathVariable(name="loginId") String loginId) {
        Member member = adminCommandService.joinAdmin(loginId);
        return ApiResponse.onSuccess(AdminConverter.adminManagerDto(member));
    }

    @PatchMapping("/managers/delete/{loginId}")
    @Operation(summary = "타 계정 관리자 삭제", description = "관리자 권한을 삭제할 member의 userId를 입력해주세요")
    public ApiResponse<AdminResponseDto.AdminManagerDto> deleteAdmin(@PathVariable(name="loginId") Long userId) {
        Member member = adminCommandService.deleteAdmin(userId);
        return ApiResponse.onSuccess(AdminConverter.adminManagerDto(member));
    }

    @GetMapping("/managers")
    @Operation(summary = "관리자 목록 조회", description = "현재 관리자 목록을 조회힙니다")
    public ApiResponse<List<AdminResponseDto.AdminManagerDto>> getAdminList() {
        List<Member> members = adminQueryService.getAdmin();
        List<AdminResponseDto.AdminManagerDto> adminListDto = AdminConverter.toAdminManagerList(members);
        return ApiResponse.onSuccess(adminListDto);
    }

}
