package umc.duckmelang.domain.report.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import umc.duckmelang.domain.report.domain.ChatReport;
import umc.duckmelang.domain.report.domain.enums.ReportStatus;
import umc.duckmelang.domain.report.dto.ReportSummaryDto;

import java.util.List;

public interface ChatReportRepository extends JpaRepository<ChatReport, Long> {
    @Query("SELECT r FROM ChatReport r WHERE r.reportStatus = :status ORDER BY r.createdAt DESC")
    Page<ChatReport> findByStatusAndPageWithDate(@Param("status") ReportStatus status,
                                             Pageable pageable);

    @Query("SELECT r FROM ChatReport r WHERE r.reportStatus = :status " +
            "ORDER BY (SELECT COUNT(r2) FROM ChatReport r2 WHERE r2.chatRoom.id = r.chatRoom.id) DESC, " +
            "r.createdAt DESC")
    Page<ChatReport> findByStatusAndPageWithCount(@Param("status") ReportStatus status,
                                              Pageable pageable);

    @Query(value = "SELECT " +
            "r.id, " +
            "COUNT(r.id), " +
            "MAX(r.createdAt), " +
            "GROUP_CONCAT(DISTINCT r.reason SEPARATOR ',')" +
            ")" +
            "FROM chat_report r " +
            "WHERE r.id IN :idList " +
            "GROUP BY r.id",
            nativeQuery = true
    )
    List<Object[]> findReportSummaryByIds(@Param("idList") List<Long> idList);
}
