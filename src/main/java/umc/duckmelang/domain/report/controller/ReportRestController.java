package umc.duckmelang.domain.report.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.domain.member.domain.enums.Role;
import umc.duckmelang.domain.report.dto.ReportRequestDto;
import umc.duckmelang.domain.report.service.ReportCommandService;
import umc.duckmelang.global.apipayload.ApiResponse;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.MemberException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
@Tag(name = "Reports", description = "신고 관련 API")
@Validated
public class ReportRestController {
    private final ReportCommandService reportCommandService;
    @PostMapping("")
    @Operation(summary = "신고 API", description = "신고 대상의 memberId를 넘겨주세요 / reason: INAPPR(\"INAPPROPRIATE\")," +
            "INSULT, SEXUAL(\"SEXUAL HARRASMENT\"), ADVERT(\"ADVERTISEMENT\"), FRAUD, ETC / " +
            "dtype: CHAT,PROFILE,POST,REVIEW")
    public ApiResponse<String> reportProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @RequestBody ReportRequestDto.reportDto request) {
        Long reporterId = userDetails.getMemberId();
        reportCommandService.report(reporterId, request);
        return ApiResponse.onSuccess("신고 완료");
    }

    @DeleteMapping("")
    @Operation(summary = "신고 삭제 API", description = "삭제하고자 하는 신고 id를 배열로 넘겨주세요")
    public ApiResponse<String> deleteReport(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @RequestBody ReportRequestDto.deleteRequestDto request) {
        if(userDetails.getRole() != Role.ADMIN)
            throw new MemberException(ErrorStatus._FORBIDDEN);

        reportCommandService.delete(request);

        return ApiResponse.onSuccess("신고 삭제");
    }
}
