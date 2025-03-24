package umc.duckmelang.domain.idolcategory.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.duckmelang.domain.idolcategory.converter.IdolCategoryConverter;
import umc.duckmelang.domain.idolcategory.domain.IdolCategory;
import umc.duckmelang.domain.idolcategory.dto.IdolCategoryResponseDto;
import umc.duckmelang.domain.idolcategory.service.IdolCategoryQueryService;
import umc.duckmelang.global.apipayload.annotations.CommonApiResponses;
import umc.duckmelang.global.apipayload.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/idols")
@RequiredArgsConstructor
public class IdolCategoryRestController {

    private final IdolCategoryQueryService idolCategoryQueryService;

    @GetMapping("")
    @CommonApiResponses
    @Operation(summary = "전체 아이돌 목록 조회 API", description = "전체 목록을 한번에 조회합니다. 추후 아이돌 수가 많아진다면 무한 스크롤 방식으로 수정하겠습니다!")
    public ApiResponse<IdolCategoryResponseDto.IdolListDto> getAllIdolList() {
        List<IdolCategory> idolCategoryList = idolCategoryQueryService.getAllIdolList();
        return ApiResponse.onSuccess(IdolCategoryConverter.toIdolListDto(idolCategoryList));
    }
}
