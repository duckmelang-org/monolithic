package umc.duckmelang.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ReportSummaryDto {
        private final Long keyId;
        private final Integer count;
        private final LocalDateTime latestDate;
        private final String[] reasons;
}
