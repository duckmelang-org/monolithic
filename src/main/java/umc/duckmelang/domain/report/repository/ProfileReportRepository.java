package umc.duckmelang.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.duckmelang.domain.report.domain.ProfileReport;

public interface ProfileReportRepository extends JpaRepository<ProfileReport, Long> {
}
