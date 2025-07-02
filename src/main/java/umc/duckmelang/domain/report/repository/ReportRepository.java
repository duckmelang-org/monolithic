package umc.duckmelang.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import umc.duckmelang.domain.report.domain.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
