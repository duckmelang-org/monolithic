package umc.duckmelang.domain.member.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.duckmelang.domain.member.domain.enums.Gender;

import java.time.LocalDate;
import java.util.List;

public class MemberResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileResultDto{
        private Long memberId;
        private String nickname;
        private LocalDate birth;
        private Gender gender;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectIdolsResultDto{
        private Long memberId;
        private List<Long> idolCategoryIds;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectEventsResultDto {
        private Long memberId;
        private List<Long> eventCategoryIds;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateLandmineResultDto {
        private Long memberId;
        private List<String> landmineContents;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateMemberProfileImageResultDto {
        private Long memberId;
        private String memberProfileImageURL;
        private boolean isPublic;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateIntroductionResultDto {
        private Long memberId;
        private String introduction;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckNicknameResponseDto{
        private boolean isAvailable;
        private String message;
    }
}
