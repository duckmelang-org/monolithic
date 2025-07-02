package umc.duckmelang.domain.report.service;

import umc.duckmelang.domain.report.dto.ReportRequestDto;

public interface ReportCommandService {
    void report(Long memberId, ReportRequestDto.reportDto dto);
    void delete(ReportRequestDto.deleteRequestDto dto);
}
