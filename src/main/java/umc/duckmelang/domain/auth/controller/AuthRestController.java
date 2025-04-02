package umc.duckmelang.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.duckmelang.domain.auth.dto.request.AuthRequestDto;
import umc.duckmelang.domain.auth.dto.request.LoginRequest;
import umc.duckmelang.domain.auth.dto.response.LoginResponse;
import umc.duckmelang.domain.auth.service.AuthService;
import umc.duckmelang.domain.auth.service.strategy.SocialLoginStrategy;
import umc.duckmelang.domain.member.domain.enums.LoginType;
import umc.duckmelang.domain.member.service.mypage.MyPageCommandService;
import umc.duckmelang.global.apipayload.ApiResponse;
import umc.duckmelang.domain.auth.user.CustomUserDetails;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthRestController {
    private final AuthService authService;
    private final MyPageCommandService myPageCommandService;

    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "RefreshToken과 AccessToken을 발급합니다.")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.onSuccess(authService.login(request.email(), request.password()));
    }

    @GetMapping("/login/{loginType}")
    @Operation(summary = "소셜 로그인", description = "구글/카카오/네이버 소셜 로그인을 진행합니다.")
    public ApiResponse<LoginResponse> kakaoLogin(@PathVariable LoginType loginType, @RequestParam String code) {
        LoginResponse loginResponse= authService.socialLogin(loginType, code);
        return ApiResponse.onSuccess(loginResponse);
    }

    @PostMapping("/token/refresh")
    @Operation(summary = "토큰 재발급 API", description = "유효한 refreshToken을 사용해 AccessToken을 재발급합니다.")
    public ApiResponse<LoginResponse> refreshToken(@RequestBody AuthRequestDto.RefreshTokenRequestDto request) {
        return ApiResponse.onSuccess(authService.reissue(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API", description = "로그아웃 처리합니다.")
    public ApiResponse<String> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.logout(userDetails.getMemberId());
        return ApiResponse.onSuccess("로그아웃되었습니다.");
    }

    @Operation(summary = "설정 - 회원 탈퇴 API", description = "회원 탈퇴를 처리합니다.")
    @DeleteMapping("/account/delete")
    public ApiResponse<String> deleteMember(@AuthenticationPrincipal CustomUserDetails userDetails){
        myPageCommandService.deleteMember(userDetails.getMemberId());
        return ApiResponse.onSuccess("성공적으로 탈퇴했습니다.");
    }
}