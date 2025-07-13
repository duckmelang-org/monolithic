package umc.duckmelang.domain.report.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.duckmelang.domain.report.domain.ChatReport;
import umc.duckmelang.domain.report.domain.ReviewReport;
import umc.duckmelang.domain.report.domain.enums.ReportStatus;

import java.util.List;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
    @Query("SELECT r FROM ReviewReport r WHERE r.reportStatus = :status ORDER BY r.createdAt DESC")
    Page<ReviewReport> findByStatusAndPageWithDate(@Param("status") ReportStatus status,
                                                 Pageable pageable);

    @Query("SELECT r FROM ReviewReport r WHERE r.reportStatus = :status " +
            "ORDER BY (SELECT COUNT(r2) FROM ReviewReport r2 WHERE r2.review.id = r.review.id) DESC, " +
            "r.createdAt DESC")
    Page<ReviewReport> findByStatusAndPageWithCount(@Param("status") ReportStatus status,
                                                  Pageable pageable);
    @Query(value = "SELECT " +
            "r.review_id, " +
            "COUNT(r.review_id), " +
            "CAST(MAX(r2.created_at) AS DATETIME), " +
            "GROUP_CONCAT(DISTINCT r2.reason SEPARATOR ',') " +
            "FROM review_report r JOIN report r2 USING (report_id) " +
            "WHERE r.report_id IN :idList " +
            "GROUP BY r.review_id",
            nativeQuery = true
    )
    List<Object[]> findReportSummaryByIds(@Param("idList") List<Long> idList);
}
