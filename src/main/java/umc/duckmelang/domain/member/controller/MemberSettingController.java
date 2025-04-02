package umc.duckmelang.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.domain.landmine.dto.LandmineResponseDto;
import umc.duckmelang.domain.landmine.service.LandmineQueryService;
import umc.duckmelang.domain.member.converter.MemberIdolConverter;
import umc.duckmelang.domain.member.domain.MemberIdol;
import umc.duckmelang.domain.member.dto.memberIdol.MemberIdolResponseDto;
import umc.duckmelang.domain.member.service.memberIdol.MemberIdolQueryService;
import umc.duckmelang.global.apipayload.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "MySettings", description = "내가 설정한 항목들을 조회하는 API")
public class MemberSettingController {
    private final LandmineQueryService landmineQueryService;
    private final MemberIdolQueryService memberIdolQueryService;

    @Operation(summary = "내가 설정한 관심 아이돌 목록 조회 API", description = "현재 내가 설정한 관심 있는 아이돌 목록을 조회합니다.")
    @GetMapping("/post/idols")
    public ApiResponse<MemberIdolResponseDto.IdolListDto> getSelectIdolResult(@AuthenticationPrincipal CustomUserDetails userDetails){
        List<MemberIdol> memberIdolList = memberIdolQueryService.getIdolListByMember(userDetails.getMemberId());
        return ApiResponse.onSuccess(MemberIdolConverter.toIdolListDto(memberIdolList));
    }

    @Operation(summary = "내가 설정한 지뢰 키워드 목록 조회 API", description = "사용자가 설정한 지뢰 키워드를 조회하는 API입니다.")
    @GetMapping("/mypage/landmines")
    public ApiResponse<LandmineResponseDto.LandmineListDto> getLandmineList(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ApiResponse.onSuccess(landmineQueryService.getLandmineList(userDetails.getMemberId()));
    }
}
