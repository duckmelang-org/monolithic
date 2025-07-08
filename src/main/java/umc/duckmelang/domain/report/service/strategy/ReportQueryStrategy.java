package umc.duckmelang.domain.report.service.strategy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import umc.duckmelang.domain.report.domain.Report;
import umc.duckmelang.domain.report.domain.enums.QueryOrder;
import umc.duckmelang.domain.report.domain.enums.ReportStatus;
import umc.duckmelang.domain.report.domain.enums.ReportType;
import umc.duckmelang.domain.report.dto.ReportResponseDto;
import umc.duckmelang.domain.report.dto.ReportSummaryDto;

import java.util.*;

public interface ReportQueryStrategy<T extends Report> {
    Page<T> queryByDateOrder(ReportStatus status, Pageable page);
    Page<T> queryByCountOrder(ReportStatus status, Pageable page);

    // 각 page의 신고의 신고 수, 가장 최신 신고 날짜, 신고 사유
    List<ReportSummaryDto> getReportSummaryDtoList(Page<? extends Report> page);

    ReportResponseDto.ReportResponseListDto convertToResponseList(Page<T> page, Map<Long, ReportSummaryDto> summaryDtoMap);

    ReportType getSupportedType();

    default Page<T> queryReports(ReportStatus status, QueryOrder order, Pageable page) {
        return switch (order) {
            case DATE -> queryByDateOrder(status, page);
            case COUNT -> queryByCountOrder(status, page);
        };
    }
}
