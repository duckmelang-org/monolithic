package umc.duckmelang.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.duckmelang.domain.auth.dto.request.LoginRequest;
import umc.duckmelang.domain.auth.dto.response.LoginResponse;
import umc.duckmelang.domain.auth.service.AuthService;
import umc.duckmelang.global.apipayload.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "인증 API")
public class AuthRestController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "로그인 후 AccessToken을 발급합니다.")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.onSuccess(authService.login(request.loginId(), request.password()));
    }
}