package umc.duckmelang.domain.landmine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.duckmelang.domain.landmine.converter.LandmineConverter;
import umc.duckmelang.domain.landmine.domain.Landmine;
import umc.duckmelang.domain.landmine.dto.LandmineRequestDto;
import umc.duckmelang.domain.landmine.dto.LandmineResponseDto;
import umc.duckmelang.domain.landmine.service.LandmineCommandService;
import umc.duckmelang.domain.landmine.service.LandmineQueryService;
import umc.duckmelang.global.apipayload.ApiResponse;
import umc.duckmelang.domain.auth.user.CustomUserDetails;

@RestController
@RequestMapping("/mypage/landmines")
@Tag(name="Landmines", description = "지뢰 관련 API")
@RequiredArgsConstructor
public class LandmineRestController {
    private final LandmineCommandService landmineCommandService;

    @Operation(summary = "지뢰 키워드 추가 API", description = "사용자가 지뢰 키워드를 추가하는 API입니다.")
    @PostMapping("")
    public ApiResponse<LandmineResponseDto.LandmineDto> addLandmine(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid LandmineRequestDto.AddLandmineDto request){
        Landmine landmine = landmineCommandService.addLandmine(userDetails.getMemberId(), request.getContent());
        return ApiResponse.onSuccess(LandmineConverter.toLandmineDto(landmine));
    }

    @Operation(summary = "지뢰 키워드 삭제 API", description = "사용자가 지뢰 키워드를 삭제하는 API입니다.")
    @DeleteMapping("/{landmineId}")
    ApiResponse<String> removeLandmine(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long landmineId){
        landmineCommandService.removeLandmine(userDetails.getMemberId(), landmineId);
        return ApiResponse.onSuccess("해당 지뢰 키워드를 삭제했습니다.");
    }
}
