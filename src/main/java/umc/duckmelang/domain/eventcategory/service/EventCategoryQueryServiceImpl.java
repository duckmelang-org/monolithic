package umc.duckmelang.domain.eventcategory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.eventcategory.converter.EventCategoryConverter;
import umc.duckmelang.domain.eventcategory.domain.EventCategory;
import umc.duckmelang.domain.eventcategory.dto.EventCategoryResponseDto;
import umc.duckmelang.domain.eventcategory.repository.EventCategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventCategoryQueryServiceImpl implements EventCategoryQueryService{
    private final EventCategoryRepository eventCategoryRepository;
    private final EventCategoryConverter eventCategoryConverter;

    @Override
    public List<EventCategoryResponseDto.EventCategoryDto> getGroupedCategories() {
        List<EventCategory> eventCategories = eventCategoryRepository.findAll();
        return eventCategoryConverter.groupCategoriesByKind(eventCategories);
    }

    @Override
    public List<EventCategory> getAllEventCategoryList() {
        return eventCategoryRepository.findAll();
    }

}
