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
import java.util.function.Function;
import java.util.stream.Collectors;

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

    default ReportResponseDto.ReportResponseListDto getReportResponseList(
            ReportStatus status, QueryOrder order, Pageable page) {
        //type에 따라 다른 Repository에서 조회하는데,
        //page, status는 jpa 변수로 넘겨주고
        //order에 따라 다른 레포지토리 함수
        Page<T> reportPage = queryReports(status, order, page);
        List<ReportSummaryDto> list = getReportSummaryDtoList(reportPage);
        Map<Long, ReportSummaryDto> reportSummaryMap = list.stream()
                .collect(Collectors.toMap(ReportSummaryDto::getReportId, Function.identity()));
        return convertToResponseList(reportPage, reportSummaryMap);
    }
}
