package umc.duckmelang.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.duckmelang.domain.eventcategory.converter.EventCategoryConverter;
import umc.duckmelang.domain.eventcategory.domain.EventCategory;
import umc.duckmelang.domain.eventcategory.dto.EventCategoryResponseDto;
import umc.duckmelang.domain.eventcategory.service.EventCategoryQueryService;
import umc.duckmelang.domain.landmine.domain.Landmine;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import umc.duckmelang.domain.member.converter.MemberConverter;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.dto.MemberRequestDto;
import umc.duckmelang.domain.member.dto.MemberResponseDto;
import umc.duckmelang.domain.member.dto.MemberSignUpDto;
import umc.duckmelang.domain.member.service.member.MemberCommandService;
import umc.duckmelang.domain.memberevent.domain.MemberEvent;
import umc.duckmelang.domain.memberidol.domain.MemberIdol;
import umc.duckmelang.domain.memberprofileimage.domain.MemberProfileImage;
import umc.duckmelang.domain.memberprofileimage.service.MemberProfileImageCommandService;
import umc.duckmelang.global.apipayload.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberRestController {
    private final MemberCommandService memberCommandService;
    private final MemberProfileImageCommandService memberProfileImageCommandService;
    private final EventCategoryQueryService eventCategoryQueryService;

    @Operation(summary = "회원가입 API", description = "사용자 정보를 받아 회원가입을 처리하는 API입니다.")
    @PostMapping("/signup")
    public ApiResponse<MemberSignUpDto.SignupResultDto> signup(@RequestBody @Valid MemberSignUpDto.SignupDto request){
        Member member = memberCommandService.signupMember(request);
        return ApiResponse.onSuccess(MemberConverter.toSignupResultDto(member));
    }

    @Operation(summary = "닉네임, 생년월일, 성별 설정 API", description = "회원이 닉네임, 생년월일, 성별을 설정하는 API입니다.")
    @PatchMapping("/{memberId}/profile")
    public ApiResponse<MemberResponseDto.ProfileResultDto> registerProfile(@PathVariable(name = "memberId") Long memberId, @RequestBody @Valid MemberRequestDto.ProfileRequestDto request){
        Member member = memberCommandService.registerProfile(memberId, request);
        return ApiResponse.onSuccess(MemberConverter.toProfileResponseDto(member));
    }

    @Operation(summary = "덕질하는 아이돌 선택 API", description = "회원이 덕질하는 아이돌(들)을 선택하는 API입니다. 최소 하나의 아이돌은 선택해야 합니다.")
    @PostMapping("/{memberId}/idols")
    public ApiResponse<MemberResponseDto.SelectIdolsResultDto> selectIdols(@PathVariable(name = "memberId") Long memberId, @RequestBody @Valid MemberRequestDto.SelectIdolsDto request) {
        List<MemberIdol> updatedMemberIdolList = memberCommandService.selectIdols(memberId, request);
        if (updatedMemberIdolList.isEmpty()) {
            throw new IllegalArgumentException("선택된 아이돌 카테고리가 없습니다.");}
        return ApiResponse.onSuccess(MemberConverter.toSelectIdolResponseDto(updatedMemberIdolList));
    }

    @Operation(summary = "행사 종류 조회 API", description = "행사의 종류를 모두 조회해옵니다.")
    @GetMapping("/events")
    public ApiResponse<EventCategoryResponseDto.EventCategoryListDto> getAllEventCategoryList() {
        List<EventCategory> idolCategoryList = eventCategoryQueryService.getAllEventCategoryList();
        return ApiResponse.onSuccess(EventCategoryConverter.toEventCategoryListDto(idolCategoryList));
    }



    @Operation(summary = "관심있는 행사 종류 선택 API", description = "회원이 관심있는 행사(들)를 선택하는 API입니다. 하나도 선택하지 않을 수 있습니다.(이 경우 빈 리스트를 반환합니다)")
    @PostMapping("/{memberId}/events")
    public ApiResponse<MemberResponseDto.SelectEventsResultDto> selectEvents(@PathVariable(name = "memberId") Long memberId, @RequestBody @Valid MemberRequestDto.SelectEventsDto request) {
        List<MemberEvent> updatedMemberEventList = memberCommandService.selectEvents(memberId, request);
        return ApiResponse.onSuccess(MemberConverter.toSelectEventResponseDto(updatedMemberEventList));
    }

    @Operation(summary = "지뢰 설정 API", description = "회원이 기피하는 키워드(들)를 선택하는 API입니다. 하나도 선택하지 않을 수 있습니다.(이 경우 빈 리스트를 반환합니다)")
    @PostMapping("/{memberId}/landmines")
    public ApiResponse<MemberResponseDto.CreateLandmineResultDto> createLandmines(@PathVariable(name = "memberId") Long memberId, @RequestBody @Valid MemberRequestDto.CreateLandminesDto request) {
        List<Landmine> updatedLandmineList = memberCommandService.createLandmines(memberId, request);
        return ApiResponse.onSuccess(MemberConverter.toCreateLandmineResponseDto(updatedLandmineList));
    }

    @Operation(summary = "프로필 사진 설정 API", description = "회원이 최초로 프로필 사진을 설정하는 API입니다. 프로필 사진을 설정하지 않을 경우 기본 프로필이 설정됩니다.")
    @PostMapping(value = "/{memberId}/profile/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<MemberResponseDto.CreateMemberProfileImageResultDto> createMemberProfileImage(
            @PathVariable(name = "memberId") Long memberId,
            @RequestPart(required = false) MultipartFile profileImage) {
        MemberProfileImage updatedMemberProfileImage = memberProfileImageCommandService.createProfileImage(memberId, profileImage);
        return ApiResponse.onSuccess(MemberConverter.toCreateMemberProfileImageResponseDto(updatedMemberProfileImage));
    }

    @Operation(summary = "자기소개 문구 설정 API", description = "회원이 최초로 자기소개 문구를 설정하는 API입니다. 빈 문자열과 공백을 허용하지 않습니다. 최대 500자 작성 가능합니다.")
    @PatchMapping("/{memberId}/introduction")
    public ApiResponse<MemberResponseDto.CreateIntroductionResultDto> createIntroduction (@PathVariable(name = "memberId") Long memberId, @RequestBody @Valid MemberRequestDto.CreateIntroductionDto request) {
        Member updatedmember = memberCommandService.createIntroduction(memberId, request);
        return ApiResponse.onSuccess(MemberConverter.toCreateIntroductionResponseDto(updatedmember));
    }

    @Operation(summary = "닉네임 중복 확인 API" ,description = "사용자가 입력한 닉네임이 이미 존재하는지 확인하는 API입니다. 닉네임은 공백일 수도 없습니다.")
    @GetMapping("/check/nickname")
    public ApiResponse<MemberResponseDto.CheckNicknameResponseDto> checkNickname(@RequestParam String nickname){
        boolean exists = memberCommandService.isNicknameExists(nickname);
        MemberResponseDto.CheckNicknameResponseDto responseDto = new MemberResponseDto.CheckNicknameResponseDto(
                !exists,
                exists ? "이미 사용 중인 닉네임입니다." : "사용 가능한 닉네임입니다."
        );

        return ApiResponse.onSuccess(responseDto);
    }
}
