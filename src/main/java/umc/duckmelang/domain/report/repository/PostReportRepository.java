package umc.duckmelang.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.duckmelang.domain.report.domain.PostReport;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
}
