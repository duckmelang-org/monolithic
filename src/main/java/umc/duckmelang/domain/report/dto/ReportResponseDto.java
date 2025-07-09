package umc.duckmelang.domain.report.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import umc.duckmelang.domain.report.domain.enums.ReportStatus;

import java.util.List;
import java.time.LocalDateTime;

public class ReportResponseDto {
    @SuperBuilder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommonReportResponseDto{
        @NotNull
        private Long memberId;
        @NotNull
        private String memberNickname;
        @NotNull
        private String[] reasons;
        @NotNull
        private Integer count;
        @NotNull
        private LocalDateTime latestDate;
        @NotNull
        private ReportStatus reportStatus;
    }

    @SuperBuilder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostReportResponseDto extends CommonReportResponseDto {
        @NotNull
        private Long postId;
        @NotNull
        private String postTitle;
        @NotNull
        private String postContent;
        @NotNull
        private LocalDateTime createdAt;
    }
    @SuperBuilder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomReportResponseDto extends CommonReportResponseDto {
        @NotNull
        private Long chatRoomId;
    }
    @SuperBuilder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewReportResponseDto extends CommonReportResponseDto {
        @NotNull
        private Long reviewId;
    }

    @SuperBuilder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileReportResponseDto extends CommonReportResponseDto {
        @NotNull
        private String savedNickname;

        @NotNull
        private String savedIntroduction;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportResponseListDto{
        List<? extends CommonReportResponseDto> list;

        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;
        Integer currentPage;
    }
}
