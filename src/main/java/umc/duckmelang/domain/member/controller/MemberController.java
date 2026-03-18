package umc.duckmelang.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.domain.member.converter.MemberConverter;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.dto.MemberSignUpDto;
import umc.duckmelang.domain.member.service.MemberService;
import umc.duckmelang.global.apipayload.ApiResponse;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/member")
@Tag(name="Member", description = "유저 API")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입 API", description = "사용자 정보를 받아 회원가입을 처리하는 API입니다.")
    @PostMapping("/signup")
    public ApiResponse<MemberSignUpDto.SignupResultDto> signup(@RequestBody @Valid MemberSignUpDto.SignupDto request){
        Member member = memberService.signupMember(request);
        return ApiResponse.onSuccess(MemberConverter.toSignupResultDto(member));
    }

    @Operation(summary = "FCM 토큰 저장 API", description = "앱 실행 시 발급된 FCM 토큰을 저장합니다.")
    @PostMapping("/fcm-token")
    public ApiResponse<Void> updateFcmToken(@RequestBody Map<String, String> body, Principal principal) {
        CustomUserDetails userDetails =
                (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        memberService.updateFcmToken(userDetails.getMemberId(), body.get("fcmToken"));
        return ApiResponse.onSuccess(null);
    }
}
