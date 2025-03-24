package umc.duckmelang.domain.member.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.duckmelang.domain.member.domain.enums.Gender;

public class MemberFilterDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilterRequestDto{
        private Gender gender;
        private Integer minAge;
        private Integer maxAge;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilterResponseDto{
        private Gender gender;
        private Integer minAge;
        private Integer maxAge;
    }
}
