package umc.duckmelang.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.duckmelang.domain.report.domain.ChatReport;

public interface ChatReportRepository extends JpaRepository<ChatReport, Long> {
}
