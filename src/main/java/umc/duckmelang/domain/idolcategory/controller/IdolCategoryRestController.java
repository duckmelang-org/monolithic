package umc.duckmelang.domain.idolcategory.controller;

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
import umc.duckmelang.domain.member.converter.MemberIdolConverter;
import umc.duckmelang.domain.member.dto.memberIdol.MemberIdolResponseDto;
import umc.duckmelang.domain.member.service.memberIdol.MemberIdolCommandService;
import umc.duckmelang.global.apipayload.ApiResponse;

import java.util.List;

@RestController
@Tag(name = "Idols", description = "아이돌 관련 API")
@RequiredArgsConstructor
public class IdolCategoryRestController {
    private final IdolCategoryQueryService idolCategoryQueryService;
    private final MemberIdolCommandService memberIdolCommandService;

    @GetMapping("/idols")
    @Operation(summary = "전체 아이돌 목록 조회 API", description = "전체 목록을 한번에 조회합니다. 추후 아이돌 수가 많아진다면 무한 스크롤 방식으로 수정하겠습니다!")
    public ApiResponse<IdolCategoryResponseDto.IdolListDto> getAllIdolList() {
        List<IdolCategory> idolCategoryList = idolCategoryQueryService.getAllIdolList();
        return ApiResponse.onSuccess(IdolCategoryConverter.toIdolListDto(idolCategoryList));
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

    @Operation(summary = "관심 아이돌 추가 API", description = "관심 아이돌을 추가하는 API입니다.")
    @PostMapping("/mypage/idols/{idolId}")
    public ApiResponse<MemberIdolResponseDto.IdolDto> addMemberIdol(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("idolId") Long idolId){
        return ApiResponse.onSuccess(MemberIdolConverter.toIdolDto(memberIdolCommandService.addMemberIdol(userDetails.getMemberId(), idolId)));
    }
}
