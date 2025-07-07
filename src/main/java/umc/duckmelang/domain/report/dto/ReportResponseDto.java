package umc.duckmelang.domain.report.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.bytebuddy.implementation.bind.annotation.Super;

import java.util.HashMap;
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
        private HashMap<String, Integer> reasons;
        @NotNull
        private Integer count;
        @NotNull
        private LocalDateTime time;
        @NotNull
        private String reportStatus;
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
    public static class ReivewReportResponseDto extends CommonReportResponseDto {
        @NotNull
        private Long reviewId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportResponseListDto{
        List<CommonReportResponseDto> list;

        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;
        Integer currentPage;
    }
}
