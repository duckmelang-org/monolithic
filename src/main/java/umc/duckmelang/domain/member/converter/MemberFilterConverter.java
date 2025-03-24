package umc.duckmelang.domain.member.converter;

import org.springframework.stereotype.Component;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.dto.member.MemberFilterDto;

@Component
public class MemberFilterConverter {

    public static MemberFilterDto.FilterResponseDto toFilterResponseDto(Member member) {
        return new MemberFilterDto.FilterResponseDto(
                member.getFilterGender() != null ? member.getFilterGender() : null,
                member.getFilterMinAge(),
                member.getFilterMaxAge()
        );
    }

    public static void applyFilterRequest(Member member, MemberFilterDto.FilterRequestDto request){
        member.updateFilter(request.getGender(), request.getMinAge(), request.getMaxAge());
    }
}
