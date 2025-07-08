package umc.duckmelang.domain.report.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.duckmelang.domain.report.domain.Report;
import umc.duckmelang.domain.report.domain.enums.ReportStatus;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    @Query("SELECT r FROM Report r WHERE r.reportStatus = :status ORDER BY r.createdAt DESC")
    Page<Report> findByStatusAndPageWithDate(@Param("status") ReportStatus status,
                                             Pageable pageable);

    @Query("SELECT r FROM Report r WHERE r.reportStatus = :status " +
            "ORDER BY (SELECT COUNT(r2) FROM Report r2 WHERE r2.receiver.id = r.receiver.id) DESC, " +
            "r.createdAt DESC")
    Page<Report> findByStatusAndPageWithCount(@Param("status") ReportStatus status,
                                              Pageable pageable);

    @Query(value = "SELECT " +
            "r.id, " +
            "COUNT(r.id), " +
            "MAX(r.createdAt), " +
            "GROUP_CONCAT(DISTINCT r.reason SEPARATOR ',')" +
            ")" +
            "FROM report r " +
            "WHERE r.id IN :idList " +
            "GROUP BY r.id",
            nativeQuery = true
    )
    List<Object[]> findReportSummaryByIds(@Param("idList") List<Long> idList);
}
