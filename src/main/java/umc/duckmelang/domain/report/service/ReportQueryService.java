package umc.duckmelang.domain.report.service;

import umc.duckmelang.domain.report.domain.enums.QueryOrder;
import umc.duckmelang.domain.report.domain.enums.ReportStatus;
import umc.duckmelang.domain.report.domain.enums.ReportType;
import umc.duckmelang.domain.report.dto.ReportResponseDto;

public interface ReportQueryService {
    ReportResponseDto.ReportResponseListDto getReportResponseList(ReportType type, ReportStatus status, QueryOrder order, Integer page);
}
