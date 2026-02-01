package umc.duckmelang.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.duckmelang.domain.member.converter.MemberConverter;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.dto.MemberSignUpDto;
import umc.duckmelang.domain.member.service.MemberCommandService;
import umc.duckmelang.global.apipayload.ApiResponse;

@RestController
@RequestMapping("/api/v1/member")
@Tag(name="Member", description = "유저 API")
@RequiredArgsConstructor
public class MemberController {

    private final MemberCommandService memberCommandService;

    @Operation(summary = "회원가입 API", description = "사용자 정보를 받아 회원가입을 처리하는 API입니다.")
    @PostMapping("/signup")
    public ApiResponse<MemberSignUpDto.SignupResultDto> signup(@RequestBody @Valid MemberSignUpDto.SignupDto request){
        Member member = memberCommandService.signupMember(request);
        return ApiResponse.onSuccess(MemberConverter.toSignupResultDto(member));
    }
}
