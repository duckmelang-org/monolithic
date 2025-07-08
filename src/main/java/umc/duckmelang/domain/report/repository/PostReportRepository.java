package umc.duckmelang.domain.report.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.duckmelang.domain.report.domain.PostReport;
import umc.duckmelang.domain.report.domain.Report;
import umc.duckmelang.domain.report.domain.enums.ReportStatus;

import java.util.List;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    @Query("SELECT r FROM PostReport r WHERE r.reportStatus = :status ORDER BY r.createdAt DESC")
    Page<PostReport> findByStatusAndPageWithDate(@Param("status") ReportStatus status,
                                             Pageable pageable);

    @Query("SELECT r FROM PostReport r WHERE r.reportStatus = :status " +
            "ORDER BY (SELECT COUNT(r2) FROM PostReport r2 WHERE r2.post.id = r.post.id) DESC, " +
            "r.createdAt DESC")
    Page<PostReport> findByStatusAndPageWithCount(@Param("status") ReportStatus status,
                                              Pageable pageable);

    @Query(value = "SELECT " +
            "r.id, " +
            "COUNT(r.id), " +
            "MAX(r.createdAt), " +
            "GROUP_CONCAT(DISTINCT r.reason SEPARATOR ',')" +
            ")" +
            "FROM post_report r " +
            "WHERE r.id IN :idList " +
            "GROUP BY r.id",
            nativeQuery = true
    )
    List<Object[]> findReportSummaryByIds(@Param("idList") List<Long> idList);

}