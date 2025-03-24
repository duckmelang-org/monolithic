package umc.duckmelang.domain.member.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import umc.duckmelang.domain.member.domain.enums.Gender;

import java.time.LocalDate;
import java.util.List;

public class MemberRequestDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ProfileRequestDto{
        @NotBlank(message = "닉네임을 입력해주세요.")
        private String nickname;
        @NotNull(message = "생년월일을 입력해주세요.")
        private LocalDate birth;
        @NotNull(message = "성별을 선택해주세요.")
        private Gender gender;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SelectIdolsDto{
        @NotEmpty(message = "최소 하나의 아이돌을 선택해야 합니다.")
        @Size(min = 1, message = "최소 하나의 아이돌을 선택해야 합니다.")
        private List<Long> idolCategoryIds;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SelectEventsDto{
        private List<Long> eventCategoryIds;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateLandminesDto {
        private List<String> landmineContents;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateIntroductionDto {
        @NotBlank(message = "자기소개를 비워둘 수 없습니다.")
        @Size(max = 500, message = "자기소개는 500자를 초과할 수 없습니다.")
        private String introduction;
    }
}
