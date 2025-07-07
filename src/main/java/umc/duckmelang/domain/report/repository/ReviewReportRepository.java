package umc.duckmelang.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.duckmelang.domain.report.domain.ReviewReport;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
}
