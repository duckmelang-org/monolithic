package umc.duckmelang.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class MemberSignUpDto {
    @Getter
    public static class SignupDto {
        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Pattern(
                regexp = "^[0-9a-zA-Z._%+-]+@[0-9a-zA-Z.-]+\\.[a-zA-Z]{2,}$",
                message = "유효한 이메일 주소 형식이 아닙니다.")
        @Schema(description = "사용자의 이메일 주소", example = "test@example.com")
        private String email;

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
        boolean profileComplete;
    }
}
