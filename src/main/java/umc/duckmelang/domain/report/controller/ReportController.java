package umc.duckmelang.domain.report.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.domain.report.dto.ReportRequestDto;
import umc.duckmelang.domain.report.service.ReportCommandService;
import umc.duckmelang.global.apipayload.ApiResponse;
import umc.duckmelang.global.validation.annotation.ExistPost;
import umc.duckmelang.global.validation.annotation.ExistsChatRoom;
import umc.duckmelang.global.validation.annotation.ExistsMember;
import umc.duckmelang.global.validation.annotation.ExistsReview;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
@Tag(name = "Reports", description = "신고 관련 API")
@Validated
public class ReportController {
    private final ReportCommandService reportCommandService;
    @PostMapping("/reports/members")
    @Operation(summary = "프로필 신고 API", description = "신고 대상의 memberId를 넘겨주세요\n reason은 INAPPR(\"INAPPROPRIATE\"),\n" +
            "    INSULT(\"INSULT\"),\n" +
            "    SEXUAL(\"SEXUAL HARRASMENT\"),\n" +
            "    ADVERT(\"ADVERTISEMENT\"),\n" +
            "    FRAUD(\"FRAUD\"),\n" +
            "    ETC(\"ETC\")")
    public ApiResponse<String> reportProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @RequestBody ReportRequestDto.reportDto request) {
        Long reporterId = userDetails.getMemberId();
        reportCommandService.report(reporterId, request);
        return ApiResponse.onSuccess("신고 완료");
    }
}
