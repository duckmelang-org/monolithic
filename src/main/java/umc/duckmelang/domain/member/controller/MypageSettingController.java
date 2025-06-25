package umc.duckmelang.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.domain.idolcategory.converter.IdolCategoryConverter;
import umc.duckmelang.domain.idolcategory.domain.IdolCategory;
import umc.duckmelang.domain.idolcategory.dto.IdolCategoryResponseDto;
import umc.duckmelang.domain.idolcategory.service.IdolCategoryQueryService;
import umc.duckmelang.domain.landmine.dto.LandmineResponseDto;
import umc.duckmelang.domain.landmine.service.LandmineQueryService;
import umc.duckmelang.domain.member.converter.MemberIdolConverter;
import umc.duckmelang.domain.member.domain.MemberIdol;
import umc.duckmelang.domain.member.dto.memberIdol.MemberIdolResponseDto;
import umc.duckmelang.domain.member.service.memberIdol.MemberIdolCommandService;
import umc.duckmelang.domain.member.service.memberIdol.MemberIdolQueryService;
import umc.duckmelang.global.apipayload.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "MySettings", description = "마이페이지-내 정보 변경에 해당하는 항목들을 조회하는 API")
public class MypageSettingController {
    private final LandmineQueryService landmineQueryService;
    private final MemberIdolQueryService memberIdolQueryService;
    private final IdolCategoryQueryService idolCategoryQueryService;
    private final MemberIdolCommandService memberIdolCommandService;

    @Operation(summary = "내가 설정한 관심 아이돌 목록 조회 API", description = "현재 내가 설정한 관심 있는 아이돌 목록을 조회합니다.")
    @GetMapping("/mypage/idols")
    public ApiResponse<MemberIdolResponseDto.IdolListDto> getSelectIdolResult(@AuthenticationPrincipal CustomUserDetails userDetails){
        List<MemberIdol> memberIdolList = memberIdolQueryService.getIdolListByMember(userDetails.getMemberId());
        return ApiResponse.onSuccess(MemberIdolConverter.toIdolListDto(memberIdolList));
    }

    @Operation(summary = "관심 아이돌 추가 API", description = "관심 아이돌을 추가하는 API입니다.")
    @PostMapping("/mypage/idols/{idolId}")
    public ApiResponse<MemberIdolResponseDto.IdolDto> addMemberIdol(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("idolId") Long idolId){
        return ApiResponse.onSuccess(MemberIdolConverter.toIdolDto(memberIdolCommandService.addMemberIdol(userDetails.getMemberId(), idolId)));
    }

    @Operation(summary = "관심 아이돌 삭제 API", description = "관심 아이돌 목록에서 관심 아이돌을 삭제하는 API입니다.")
    @DeleteMapping("/mypage/idols/{idolId}")
    public ApiResponse<String> deleteMemberIdol(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("idolId") Long idolId){
        memberIdolCommandService.deleteMemberIdol(userDetails.getMemberId(), idolId);
        return ApiResponse.onSuccess("해당 아이돌을 삭제했습니다.");
    }


    @Operation(summary = "아이돌 목록 검색 API", description = "키워드를 통해 관심있는 아이돌을 찾는 API입니다.")
    @GetMapping("/mypage/idols/search")
    public ApiResponse<IdolCategoryResponseDto.IdolListDto> getIdolListByKeyword(@RequestParam("keyword") String keyword){
        List<IdolCategory> idolCategoryList = idolCategoryQueryService.getIdolListByKeyword(keyword);
        return ApiResponse.onSuccess(IdolCategoryConverter.toIdolListDto(idolCategoryList));
    }

    @Operation(summary = "내가 설정한 지뢰 키워드 목록 조회 API", description = "사용자가 설정한 지뢰 키워드를 조회하는 API입니다.")
    @GetMapping("/mypage/landmines")
    public ApiResponse<LandmineResponseDto.LandmineListDto> getLandmineList(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ApiResponse.onSuccess(landmineQueryService.getLandmineList(userDetails.getMemberId()));
    }


}
