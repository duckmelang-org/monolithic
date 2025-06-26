package umc.duckmelang.domain.idolcategory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.domain.idolcategory.converter.IdolCategoryConverter;
import umc.duckmelang.domain.idolcategory.domain.IdolCategory;
import umc.duckmelang.domain.idolcategory.dto.IdolCategoryRequestDto;
import umc.duckmelang.domain.idolcategory.dto.IdolCategoryResponseDto;
import umc.duckmelang.domain.idolcategory.service.IdolCategoryCommandService;
import umc.duckmelang.domain.idolcategory.service.IdolCategoryQueryService;
import umc.duckmelang.domain.member.converter.MemberIdolConverter;
import umc.duckmelang.domain.member.domain.enums.Role;
import umc.duckmelang.domain.member.dto.memberIdol.MemberIdolResponseDto;
import umc.duckmelang.domain.member.service.memberIdol.MemberIdolCommandService;
import umc.duckmelang.global.apipayload.ApiResponse;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.MemberException;

import java.util.List;

@RestController
@Tag(name = "Idols", description = "아이돌 관련 API")
@RequiredArgsConstructor
public class IdolCategoryRestController {
    private final IdolCategoryQueryService idolCategoryQueryService;
    private final IdolCategoryCommandService idolCategoryCommandService;
    private final MemberIdolCommandService memberIdolCommandService;

    @GetMapping("/idols")
    @Operation(summary = "전체 아이돌 목록 조회 API", description = "전체 목록을 한번에 조회합니다. 추후 아이돌 수가 많아진다면 무한 스크롤 방식으로 수정하겠습니다!")
    public ApiResponse<IdolCategoryResponseDto.IdolListDto> getAllIdolList() {
        List<IdolCategory> idolCategoryList = idolCategoryQueryService.getAllIdolList();
        return ApiResponse.onSuccess(IdolCategoryConverter.toIdolListDto(idolCategoryList));
    }

    @DeleteMapping("/idols/{idolId}")
    @Operation(summary = "특정 아이돌 카테고리 삭제 API(admin)", description = "관리자 기능. 아이돌 카테고리에서 특정 아이돌을 삭제합니다.")
    public ApiResponse<String> deleteIdol(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("idolId") Long idolId){
        if(userDetails.getRole() != Role.ADMIN)
            throw new MemberException(ErrorStatus._FORBIDDEN);
        idolCategoryCommandService.deleteIdolCategory(idolId);
        return ApiResponse.onSuccess("해당 아이돌 카테고리를 삭제했습니다.");
    }

    @PostMapping(value = "/idols", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "아이돌 카테고리 추가 API(admin)", description = "관리자 기능. 아이돌 카테고리에 아이돌 이름, 이미지를 추가합니다.")
    public ApiResponse<String> createIdol(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("names") List<String> names,
                                          @RequestPart("files") List<MultipartFile> files) {
        if(userDetails.getRole() != Role.ADMIN)
            throw new MemberException(ErrorStatus._FORBIDDEN);
        idolCategoryCommandService.createIdolCategory(names, files);
        return ApiResponse.onSuccess("아이돌 카테고리를 추가했습니다.");
    }

    @PostMapping(value = "/idols/update", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "아이돌 카테고리 수정 API(admin)", description = "관리자 기능. 기존 아이돌 카테고리의 이름, 이미지를 수정합니다.")
    public ApiResponse<String> updateIdol(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestPart("dtos") IdolCategoryRequestDto.IdolCategoryRequestList request, @RequestPart("files") List<MultipartFile> files){
        if(userDetails.getRole() != Role.ADMIN)
            throw new MemberException(ErrorStatus._FORBIDDEN);
        idolCategoryCommandService.updateIdolCategory(request, files);
        return ApiResponse.onSuccess("아이돌 카테고리를 수정했습니다.");
    }
}
