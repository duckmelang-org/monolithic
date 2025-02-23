package umc.duckmelang.domain.eventcategory.service;

import umc.duckmelang.domain.eventcategory.domain.EventCategory;
import umc.duckmelang.domain.eventcategory.dto.EventCategoryResponseDto;

import java.util.List;

public interface EventCategoryQueryService {
    List<EventCategoryResponseDto.EventCategoryDto> getGroupedCategories();
    List<EventCategory> getAllEventCategoryList();
}
