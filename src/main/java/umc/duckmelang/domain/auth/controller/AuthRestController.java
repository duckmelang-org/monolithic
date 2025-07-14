package umc.duckmelang.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.duckmelang.domain.auth.dto.request.AuthRequestDto;
import umc.duckmelang.domain.auth.dto.request.KakaoLoginRequest;
import umc.duckmelang.domain.auth.dto.request.LoginRequest;
import umc.duckmelang.domain.auth.dto.request.ResetPasswordRequest;
import umc.duckmelang.domain.auth.dto.response.CheckIdResponse;
import umc.duckmelang.domain.auth.dto.response.LoginIdResponse;
import umc.duckmelang.domain.auth.dto.response.LoginResponse;
import umc.duckmelang.domain.auth.dto.response.PhoneNumResponse;
import umc.duckmelang.domain.auth.service.AuthService;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.global.apipayload.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth", description = "인증 API")
public class AuthRestController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "RefreshToken과 AccessToken을 발급합니다.")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.onSuccess(authService.login(request.loginId(), request.password()));
    }

    @PostMapping("/kakao-login")
    @Operation(summary = "카카오 로그인 API", description = "ios에서 받은 카카오 토큰으로 로그인/회원가입을 처리합니다.")
    public ApiResponse<LoginResponse> kakaoLogin(@Valid @RequestBody KakaoLoginRequest request){
        return ApiResponse.onSuccess(authService.kakaoLogin(request));
    }

    @PostMapping("/token/refresh")
    @Operation(summary = "토큰 재발급 API", description = "유효한 refreshToken을 사용해 AccessToken을 재발급합니다.")
    public ApiResponse<LoginResponse> refreshToken(@RequestBody AuthRequestDto.RefreshTokenRequestDto request) {
        return ApiResponse.onSuccess(authService.reissue(request.getRefreshToken()));
    }

    @Operation(summary = "아이디 중복 확인 API", description = "중복일 경우, true를 반환하고 중복되지 않을 경우 false를 반환합니다.")
    @GetMapping("/nickname")
    public ApiResponse<CheckIdResponse> checkLoginId(@RequestParam String loginId) {
        boolean isDuplicate = authService.isDuplicateLoginId(loginId);
        return ApiResponse.onSuccess(new CheckIdResponse(isDuplicate));
    }

    @Operation(summary = "전화번호 중복 확인 API", description = "전화번호는 숫자만 입력합니다. 중복일 경우, true를 반환하고 중복되지 않을 경우 false를 반환합니다.")
    @GetMapping("/phone")
    public ApiResponse<CheckIdResponse> checkPhoneNum(@RequestParam String phoneNum) {
        boolean isDuplicate = authService.isDuplicatePhoneNum(phoneNum);
        return ApiResponse.onSuccess(new CheckIdResponse(isDuplicate));
    }

    @Operation(summary = "아이디 찾기 API", description = "전화번호를 기준으로 아이디를 반환합니다.")
    @GetMapping("/find-id")
    public ApiResponse<LoginIdResponse> findLoginId(@RequestParam String phoneNum) {
        String email = authService.findLoginIdByPhoneNum(phoneNum);
        return ApiResponse.onSuccess(new LoginIdResponse(email));
    }

    @Operation(summary = "전화번호 등록 API", description = "db에 전화번호를 등록하는 API입니다.")
    @PostMapping("/{memberId}/phone")
    public ApiResponse<PhoneNumResponse> addPhoneNum(@PathVariable(name = "memberId") Long memberId, @RequestParam String phoneNum){
        authService.addPhoneNum(phoneNum, memberId);
        return ApiResponse.onSuccess(new PhoneNumResponse(phoneNum));
    }

    @Operation(summary = "비밀번호 찾기 API", description = "아이디를 기준으로 비밀번호를 재설정합니다.")
    @PatchMapping("/find-password")
    public ApiResponse<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.loginId(), request.newPassword());
        return ApiResponse.onSuccess("비밀번호가 성공적으로 변경되었습니다.");
    }

    @Operation(summary = "회원 탈퇴 API", description = "회원 탈퇴를 처리합니다.")
    @DeleteMapping("/me")
    public ApiResponse<String> deleteMember(@AuthenticationPrincipal CustomUserDetails userDetails){
        authService.deleteMember(userDetails.getMemberId());
        return ApiResponse.onSuccess("성공적으로 탈퇴했습니다.");
    }
}