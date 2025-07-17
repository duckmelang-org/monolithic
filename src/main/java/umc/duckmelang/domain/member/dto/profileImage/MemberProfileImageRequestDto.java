package umc.duckmelang.domain.member.dto.profileImage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberProfileImageRequestDto {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateProfileImageStatusDto{
        boolean publicStatus;
    }
}
