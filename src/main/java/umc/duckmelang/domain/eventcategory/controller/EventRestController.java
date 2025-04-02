package umc.duckmelang.domain.eventcategory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.duckmelang.domain.eventcategory.converter.EventCategoryConverter;
import umc.duckmelang.domain.eventcategory.domain.EventCategory;
import umc.duckmelang.domain.eventcategory.dto.EventCategoryResponseDto;
import umc.duckmelang.domain.eventcategory.service.EventCategoryQueryService;
import umc.duckmelang.global.apipayload.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Events", description = "행사 관련 API")
public class EventRestController {
    private final EventCategoryQueryService eventCategoryQueryService;

    @Operation(summary = "행사 종류 전체 조회 API", description = "행사의 종류를 모두 조회해옵니다.")
    @GetMapping("/events")
    public ApiResponse<EventCategoryResponseDto.EventCategoryListDto> getAllEventCategoryList() {
        List<EventCategory> idolCategoryList = eventCategoryQueryService.getAllEventCategoryList();
        return ApiResponse.onSuccess(EventCategoryConverter.toEventCategoryListDto(idolCategoryList));
    }
}
