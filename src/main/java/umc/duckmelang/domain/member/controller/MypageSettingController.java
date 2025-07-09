package umc.duckmelang.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.domain.idolcategory.converter.IdolCategoryConverter;
import umc.duckmelang.domain.idolcategory.domain.IdolCategory;
import umc.duckmelang.domain.idolcategory.dto.IdolCategoryResponseDto;
import umc.duckmelang.domain.idolcategory.service.IdolCategoryQueryService;
import umc.duckmelang.domain.landmine.converter.LandmineConverter;
import umc.duckmelang.domain.landmine.domain.Landmine;
import umc.duckmelang.domain.landmine.dto.LandmineRequestDto;
import umc.duckmelang.domain.landmine.dto.LandmineResponseDto;
import umc.duckmelang.domain.landmine.service.LandmineCommandService;
import umc.duckmelang.domain.landmine.service.LandmineQueryService;
import umc.duckmelang.domain.member.converter.MemberIdolConverter;
import umc.duckmelang.domain.member.domain.MemberIdol;
import umc.duckmelang.domain.member.dto.member.MemberFilterDto;
import umc.duckmelang.domain.member.dto.memberIdol.MemberIdolResponseDto;
import umc.duckmelang.domain.member.service.memberIdol.MemberIdolCommandService;
import umc.duckmelang.domain.member.service.memberIdol.MemberIdolQueryService;
import umc.duckmelang.domain.member.service.mypage.MyPageCommandService;
import umc.duckmelang.domain.member.service.mypage.MyPageQueryService;
import umc.duckmelang.global.apipayload.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settings")
@Tag(name = "MySettings", description = "마이페이지-내 정보 변경에 해당하는 항목들을 조회하는 API")
public class MypageSettingController {
    private final LandmineQueryService landmineQueryService;
    private final MemberIdolQueryService memberIdolQueryService;
    private final IdolCategoryQueryService idolCategoryQueryService;
    private final MemberIdolCommandService memberIdolCommandService;
    private final LandmineCommandService landmineCommandService;
    private final MyPageQueryService myPageQueryService;
    private final MyPageCommandService myPageCommandService;

    @Operation(summary = "내가 설정한 관심 아이돌 목록 조회 API", description = "현재 내가 설정한 관심 있는 아이돌 목록을 조회합니다.")
    @GetMapping("/idols")
    public ApiResponse<MemberIdolResponseDto.IdolListDto> getSelectIdolResult(@AuthenticationPrincipal CustomUserDetails userDetails){
        List<MemberIdol> memberIdolList = memberIdolQueryService.getIdolListByMember(userDetails.getMemberId());
        return ApiResponse.onSuccess(MemberIdolConverter.toIdolListDto(memberIdolList));
    }

    @Operation(summary = "관심 아이돌 추가 API", description = "관심 아이돌을 추가하는 API입니다.")
    @PostMapping("/idols/{idolId}")
    public ApiResponse<MemberIdolResponseDto.IdolDto> addMemberIdol(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("idolId") Long idolId){
        return ApiResponse.onSuccess(MemberIdolConverter.toIdolDto(memberIdolCommandService.addMemberIdol(userDetails.getMemberId(), idolId)));
    }

    @Operation(summary = "관심 아이돌 삭제 API", description = "관심 아이돌을 삭제하는 API입니다.")
    @DeleteMapping("/idols/{idolId}")
    public ApiResponse<String> deleteMemberIdol(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("idolId") Long idolId){
        memberIdolCommandService.deleteMemberIdol(userDetails.getMemberId(), idolId);
        return ApiResponse.onSuccess("해당 아이돌을 삭제했습니다.");
    }

    @Operation(summary = "아이돌 목록 검색 API", description = "키워드를 통해 관심있는 아이돌을 찾는 API입니다.")
    @GetMapping("/idols/search")
    public ApiResponse<IdolCategoryResponseDto.IdolListDto> getIdolListByKeyword(@RequestParam("keyword") String keyword){
        List<IdolCategory> idolCategoryList = idolCategoryQueryService.getIdolListByKeyword(keyword);
        return ApiResponse.onSuccess(IdolCategoryConverter.toIdolListDto(idolCategoryList));
    }

    @Operation(summary = "내가 설정한 지뢰 키워드 목록 조회 API", description = "사용자가 설정한 지뢰 키워드를 조회하는 API입니다.")
    @GetMapping("/landmines")
    public ApiResponse<LandmineResponseDto.LandmineListDto> getLandmineList(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ApiResponse.onSuccess(landmineQueryService.getLandmineList(userDetails.getMemberId()));
    }

    @Operation(summary = "지뢰 키워드 추가 API", description = "사용자가 지뢰 키워드를 추가하는 API입니다.")
    @PostMapping("/landmines")
    public ApiResponse<LandmineResponseDto.LandmineDto> addLandmine(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid LandmineRequestDto.AddLandmineDto request){
        Landmine landmine = landmineCommandService.addLandmine(userDetails.getMemberId(), request.getContent());
        return ApiResponse.onSuccess(LandmineConverter.toLandmineDto(landmine));
    }

    @Operation(summary = "지뢰 키워드 삭제 API", description = "사용자가 지뢰 키워드를 삭제하는 API입니다.")
    @DeleteMapping("/landmines/{landmineId}")
    ApiResponse<String> removeLandmine(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long landmineId){
        landmineCommandService.removeLandmine(userDetails.getMemberId(), landmineId);
        return ApiResponse.onSuccess("해당 지뢰 키워드를 삭제했습니다.");
    }

    @Operation(summary = "필터 조건 조회 API", description = "사용자가 설정한 필터 조건을 조회하는 API입니다.")
    @GetMapping("/filters")
    public ApiResponse<MemberFilterDto.FilterResponseDto> getMemberFilter(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ApiResponse.onSuccess(myPageQueryService.getMemberFilter(userDetails.getMemberId()));
    }

    @Operation(summary = "필터 조건 설정 API", description = "사용자가 필터 조건을 설정하는 API입니다. " +
            "\n" +
            "- `gender`: 특정 성별 필터링 (예: `MALE`, `FEMALE`), 설정하지 않으면 전체 조회\n" +
            "- `minAge`: 최소 나이 필터링, 해당 나이 이상의 사용자 게시글만 조회\n" +
            "- `maxAge`: 최대 나이 필터링, 해당 나이 이하의 사용자 게시글만 조회\n" +
            "- **나이 필터를 설정하지 않으려면 `null`로 요청하면 전체 조회됨**")
    @PostMapping("/filters")
    public ApiResponse<MemberFilterDto.FilterResponseDto> setFilter(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                    @RequestBody MemberFilterDto.FilterRequestDto request) {
        return ApiResponse.onSuccess(myPageCommandService.setFilter(userDetails.getMemberId(), request));
    }
}
