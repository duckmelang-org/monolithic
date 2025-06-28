package umc.duckmelang.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.duckmelang.domain.auth.dto.request.AuthRequestDto;
import umc.duckmelang.domain.auth.dto.request.LoginRequest;
import umc.duckmelang.domain.auth.dto.response.CheckIdResponse;
import umc.duckmelang.domain.auth.dto.response.LoginResponse;
import umc.duckmelang.domain.auth.service.AuthService;
import umc.duckmelang.domain.member.service.mypage.MyPageCommandService;
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

    @PostMapping("/token/refresh")
    @Operation(summary = "토큰 재발급 API", description = "유효한 refreshToken을 사용해 AccessToken을 재발급합니다.")
    public ApiResponse<LoginResponse> refreshToken(@RequestBody AuthRequestDto.RefreshTokenRequestDto request) {
        return ApiResponse.onSuccess(authService.reissue(request.getRefreshToken()));
    }

    @Operation(summary = "아이디 중복 확인 API", description = "중복일 경우, true를 반환하고 중복되지 않을 경우 false를 반환합니다.")
    @GetMapping("/nickname")
    public ApiResponse<CheckIdResponse> checkNickname(@RequestParam String loginId) {
        boolean isDuplicate = authService.isDuplicateLoginId(loginId);
        return ApiResponse.onSuccess(new CheckIdResponse(isDuplicate));
    }

    @Operation(summary = "전화번호 중복 확인 API", description = "중복일 경우, true를 반환하고 중복되지 않을 경우 false를 반환합니다.")
    @GetMapping("/phone")
    public ApiResponse<CheckIdResponse> checkPhoneNum(@RequestParam String phoneNum) {
        boolean isDuplicate = authService.isDuplicatePhoneNum(phoneNum);
        return ApiResponse.onSuccess(new CheckIdResponse(isDuplicate));
    }

//    @Operation(summary = "설정 - 회원 탈퇴 API", description = "회원 탈퇴를 처리합니다.")
//    @DeleteMapping("/account/delete")
//    public ApiResponse<String> deleteMember(@AuthenticationPrincipal CustomUserDetails userDetails){
//        myPageCommandService.deleteMember(userDetails.getMemberId());
//        return ApiResponse.onSuccess("성공적으로 탈퇴했습니다.");
//    }
}