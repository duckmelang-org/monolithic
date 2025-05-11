package umc.duckmelang.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(@NotBlank(message = "이메일은 필수 입력 항목입니다.")
                           @Pattern(
                                   regexp = "^[0-9a-zA-Z._%+-]+@[0-9a-zA-Z.-]+\\.[a-zA-Z]{2,}$",
                                   message = "유효한 이메일 주소 형식이 아닙니다.")
                           @Schema(description = "사용자의 이메일 주소", example = "test@example.com")
                           String email,
                           @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
                           String password) {
}
