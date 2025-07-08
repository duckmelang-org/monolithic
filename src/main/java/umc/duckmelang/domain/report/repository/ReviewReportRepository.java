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
            "r.id, " +
            "COUNT(r.id), " +
            "MAX(r.createdAt), " +
            "GROUP_CONCAT(DISTINCT r.reason SEPARATOR ',')" +
            ")" +
            "FROM review_report r " +
            "WHERE r.id IN :idList " +
            "GROUP BY r.id",
            nativeQuery = true
    )
    List<Object[]> findReportSummaryByIds(@Param("idList") List<Long> idList);
}
