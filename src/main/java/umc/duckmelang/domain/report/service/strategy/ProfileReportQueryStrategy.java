package umc.duckmelang.domain.report.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.report.domain.ProfileReport;
import umc.duckmelang.domain.report.domain.Report;
import umc.duckmelang.domain.report.domain.enums.ReportStatus;
import umc.duckmelang.domain.report.domain.enums.ReportType;
import umc.duckmelang.domain.report.dto.ReportResponseDto;
import umc.duckmelang.domain.report.dto.ReportSummaryDto;
import umc.duckmelang.domain.report.repository.ProfileReportRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProfileReportQueryStrategy implements ReportQueryStrategy<ProfileReport> {
    private final ProfileReportRepository repository;

    @Override
    public Page<ProfileReport> queryByDateOrder(ReportStatus status, Pageable page) {
        return repository.findByStatusAndPageWithDate(status, page);
    }

    @Override
    public Page<ProfileReport> queryByCountOrder(ReportStatus status, Pageable page) {
        return repository.findByStatusAndPageWithCount(status, page);
    }

    @Override
    public List<ReportSummaryDto> getReportSummaryDtoList(Page<? extends Report> page) {
        List<Long> idList = page.stream().map(ProfileReport::getId).toList();
        List<Object[]> results = repository.findReportSummaryByIds(idList);

        return results.stream()
                .map(row -> ReportSummaryDto.builder()
                        .reportId(((Number)row[0]).longValue())
                        .count(((Number)row[1]).intValue())
                        .latestDate((LocalDateTime) row[2])
                        .reasons(((String) row[3]).split(","))
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public ReportResponseDto.ReportResponseListDto convertToResponseList(Page<ProfileReport> page, Map<Long, ReportSummaryDto> summaryDtoMap) {
        return null;
    }

    @Override
    public ReportType getSupportedType() {
        return ReportType.PROFILE;
    }

}
