package umc.duckmelang.domain.report.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.report.domain.*;
import umc.duckmelang.domain.report.dto.ReportResponseDto;
import umc.duckmelang.domain.report.dto.ReportSummaryDto;

import java.util.stream.Collectors;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ReportConverter {
    public static ReportResponseDto.CommonReportResponseDto commonReportResponseDto(Report report, ReportSummaryDto summaryDto) {
        return ReportResponseDto.CommonReportResponseDto.builder()
                .memberId(report.getReceiver().getId())
                .memberNickname(report.getReceiver().getNickname())
                .reasons(summaryDto.getReasons())
                .latestDate(summaryDto.getLatestDate())
                .reportStatus(report.getReportStatus())
                .count(summaryDto.getCount())
                .build();
    }

    public static ReportResponseDto.PostReportResponseDto postReportResponseDto(PostReport report, ReportSummaryDto summaryDto){
        return ReportResponseDto.PostReportResponseDto.builder()
                .memberId(report.getReceiver().getId())
                .memberNickname(report.getReceiver().getNickname())
                .reasons(summaryDto.getReasons())
                .latestDate(summaryDto.getLatestDate())
                .reportStatus(report.getReportStatus())
                .postId(report.getPost().getId())
                .postTitle(report.getPost().getTitle())
                .postContent(report.getPost().getContent())
                .createdAt(report.getCreatedAt())
                .count(summaryDto.getCount())
                .build();
    }

    public static ReportResponseDto.ChatRoomReportResponseDto chatRoomReportResponseDto(ChatReport report, ReportSummaryDto summaryDto) {
        return ReportResponseDto.ChatRoomReportResponseDto.builder()
                .memberId(report.getReceiver().getId())
                .memberNickname(report.getReceiver().getNickname())
                .reasons(summaryDto.getReasons())
                .latestDate(summaryDto.getLatestDate())
                .reportStatus(report.getReportStatus())
                .chatRoomId(report.getChatRoom().getId())
                .count(summaryDto.getCount())
                .build();
    }

    public static ReportResponseDto.ReviewReportResponseDto reviewReportResponseDto(ReviewReport report, ReportSummaryDto summaryDto){
        return ReportResponseDto.ReviewReportResponseDto.builder()
                .memberId(report.getReceiver().getId())
                .memberNickname(report.getReceiver().getNickname())
                .reasons(summaryDto.getReasons())
                .latestDate(summaryDto.getLatestDate())
                .reportStatus(report.getReportStatus())
                .reviewId(report.getReview().getId())
                .count(summaryDto.getCount())
                .build();
    }

    public static ReportResponseDto.ProfileReportResponseDto profileReportResponseDto(ProfileReport report, ReportSummaryDto summaryDto) {
        return ReportResponseDto.ProfileReportResponseDto.builder()
                .memberId(report.getReceiver().getId())
                .memberNickname(report.getReceiver().getNickname())
                .reasons(summaryDto.getReasons())
                .latestDate(summaryDto.getLatestDate())
                .reportStatus(report.getReportStatus())
                .savedIntroduction(report.getIntroduction())
                .savedNickname(report.getNickname())
                .count(summaryDto.getCount())
                .build();
    }

    // Converter 수정
    public static <T extends ReportResponseDto.CommonReportResponseDto>
    ReportResponseDto.ReportResponseListDto<T> reportResponseListDto(Page<T> page) {

        List<T> reportResponseDtoList = page.getContent();

        return ReportResponseDto.ReportResponseListDto.<T>builder()
                .list(reportResponseDtoList)
                .isLast(page.isLast())
                .isFirst(page.isFirst())
                .totalPage(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .listSize(reportResponseDtoList.size())
                .currentPage(page.getNumber())
                .build();
    }


}
