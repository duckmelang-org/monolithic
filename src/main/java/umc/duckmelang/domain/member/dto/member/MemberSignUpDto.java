package umc.duckmelang.domain.member.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class MemberSignUpDto {

    @Getter
    public static class SignupDto {
        @NotBlank(message = "아이디는 필수 입력 항목입니다.")
        private String loginId;
        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        private String password;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupResultDto {
        Long memberId;
        LocalDateTime createdAt;
    }
}
