package umc.duckmelang.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class MemberSignUpDto {

    @Getter
    @NoArgsConstructor
    public static class SignupDto {
        @NotBlank(message = "아이디는 필수 입력 항목입니다.")
        private String loginId;

        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        private String password;

        @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
        private String nickname;
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
