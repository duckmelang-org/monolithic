package umc.duckmelang.domain.report.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import umc.duckmelang.domain.report.domain.enums.Reason;
import umc.duckmelang.domain.report.domain.enums.ReportStatus;
import umc.duckmelang.domain.report.domain.enums.ReportType;
import java.util.List;

public class ReportRequestDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class reportDto{
        @NotNull
        private Long id; // Chatroom, Review, Post, memberId
        @NotNull
        private Reason reason;
        @NotNull
        private String dType;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class deleteRequestDto {
        @NotNull
        private List<Long> reportIdList;
    }
}
