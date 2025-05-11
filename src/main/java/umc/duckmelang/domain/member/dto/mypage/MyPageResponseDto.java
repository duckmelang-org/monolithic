package umc.duckmelang.domain.member.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.duckmelang.domain.member.domain.enums.Gender;

public class MyPageResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPagePreviewDto {
        private Long memberId;
        private String nickname;
        private Gender gender;
        private int age;
        private String latestPublicMemberProfileImage;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPageProfileDto {
        private Long memberId;
        private String nickname;
        private Gender gender;
        private int age;
        private String latestPublicMemberProfileImage;
        private String introduction;
        private long postCount;
        private long matchCount;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPageProfileEditBeforeDto {
        private String nickname;
        private String latestPublicMemberProfileImage;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPageProfileEditAfterDto {
        private Long memberId;
        private String nickname;
        private String introduction;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginInfoDto{
        private String nickname;
        private String email;
        private boolean isKakaoLinked;
        private boolean isGoogleLinked;
    }
}
