package umc.duckmelang.domain.member.dto.mypage;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MyPageRequestDto {
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateMemberProfileDto {
        private String nickname;
        @Size(max = 500, message = "자기소개는 500자를 초과할 수 없습니다.")
        private String introduction;
    }
}
