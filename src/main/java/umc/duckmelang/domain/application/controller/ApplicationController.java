package umc.duckmelang.domain.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.duckmelang.domain.application.converter.ApplicationConverter;
import umc.duckmelang.domain.application.domain.Application;
import umc.duckmelang.domain.application.dto.request.ApplicationRequestDto;
import umc.duckmelang.domain.application.dto.response.ApplicationResponseDto;
import umc.duckmelang.domain.application.service.ApplicationService;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.global.apipayload.ApiResponse;

@RestController
@RequestMapping("/api/v1/application")
@Tag(name = "Application", description = "동행 API")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(summary = "동행 요청 API", description = "동행 요청을 하는 API입니다.")
    @PostMapping("/request")
    public ApiResponse<ApplicationResponseDto.CreateResultDto> createApplication(@RequestBody ApplicationRequestDto.CreateRequestDto request,
                                                                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        Application application = applicationService.createApplication(request, userDetails.getMemberId());
        return ApiResponse.onSuccess(ApplicationConverter.toApplicationResponseDto(application));
    }

    @Operation(summary = "동행 신청 수락 API", description = "동행 요청을 수락합니다.")
    @PatchMapping("/{applicationId}/accept")
    public ApiResponse<ApplicationResponseDto.UpdateResultDto> acceptApplication(@PathVariable(name = "applicationId") Long applicationId,
                                                                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        Application application = applicationService.acceptApplication(applicationId, userDetails.getMemberId());
        return ApiResponse.onSuccess(ApplicationConverter.toUpdateResultDto(application));
    }

    @Operation(summary = "동행 신청 거절 API", description = "동행 요청을 거절합니다.")
    @PatchMapping("/{applicationId}/refuse")
    public ApiResponse<ApplicationResponseDto.UpdateResultDto> refuseApplication(@PathVariable(name = "applicationId") Long applicationId,
                                                                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        Application application = applicationService.refuseApplication(applicationId, userDetails.getMemberId());
        return ApiResponse.onSuccess(ApplicationConverter.toUpdateResultDto(application));
    }
}
