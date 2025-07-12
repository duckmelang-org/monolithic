package umc.duckmelang.domain.report.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.report.converter.ReportConverter;
import umc.duckmelang.domain.report.domain.Report;
import umc.duckmelang.domain.report.domain.enums.ReportStatus;
import umc.duckmelang.domain.report.domain.enums.ReportType;
import umc.duckmelang.domain.report.dto.ReportResponseDto;
import umc.duckmelang.domain.report.dto.ReportSummaryDto;
import umc.duckmelang.domain.report.repository.ReportRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommonReportQueryStrategy implements ReportQueryStrategy <Report>{
    private final ReportRepository repository;

    @Override
    public Page<Report> queryByDateOrder(ReportStatus status, Pageable page) {
        return repository.findByStatusAndPageWithDate(status, page);
    }

    @Override
    public Page<Report> queryByCountOrder(ReportStatus status, Pageable page) {
        return repository.findByStatusAndPageWithCount(status, page);
    }

    @Override
    public List<ReportSummaryDto> getReportSummaryDtoList(Page<? extends Report> page) {
        List<Long> idList = page.stream().map(Report::getId).toList();
        List<Object[]> results = repository.findReportSummaryByIds(idList);

        return results.stream()
                .map(row -> ReportSummaryDto.builder()
                        .reportId(((Number)row[0]).longValue())
                        .count(((Number)row[1]).intValue())
                        .latestDate(((Timestamp) row[2]).toLocalDateTime())  // 직접 변환
                        .reasons(((String) row[3]).split(","))
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public ReportResponseDto.ReportResponseListDto convertToResponseList(Page<Report> page, Map<Long, ReportSummaryDto> summaryDtoMap) {
        return ReportConverter.reportResponseListDto(
                page.map(
                        report -> ReportConverter.commonReportResponseDto(report, summaryDtoMap.get(report.getId()))));
    }

    @Override
    public ReportType getSupportedType() {
        return ReportType.TOTAL;
    }

}
