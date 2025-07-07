package umc.duckmelang.domain.report.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.report.domain.Report;
import umc.duckmelang.domain.report.domain.enums.ReportStatus;
import umc.duckmelang.domain.report.dto.ReportResponseDto;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ReportConverter {
    public static ReportResponseDto.CommonReportResponseDto commonReportResponseDto(Member member, HashMap<String, Integer> reasons, int count, LocalDateTime time, ReportStatus status) {
        return ReportResponseDto.CommonReportResponseDto.builder()
                .memberId(member.getId())
                .memberNickname(member.getNickname())
                .reasons(reasons)
                .time(time)
                .reportStatus(status.name())
                .build();
    }

    public static ReportResponseDto.ChatRoomReportResponseDto chatRoomReportResponseDto() {
        return ReportResponseDto.ChatRoomReportResponseDto.builder()
                .build();
    }

    public static ReportResponseDto.PostReportResponseDto postReportResponseDto(){
        return ReportResponseDto.PostReportResponseDto.builder().build();
    }

    public static ReportResponseDto.ReivewReportResponseDto reivewReportResponseDto(){
        return ReportResponseDto.ReivewReportResponseDto.builder().build();
    }

    public static ReportResponseDto.ReportResponseListDto reportResponseListDto(Page<ReportResponseDto.CommonReportResponseDto> page){
        List<ReportResponseDto.CommonReportResponseDto> commonReportResponseDtoList = page.stream()
                .collect(Collectors.toList());
        return ReportResponseDto.ReportResponseListDto.builder()
                .isLast(page.isLast())
                .isFirst(page.isFirst())
                .totalPage(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .listSize(commonReportResponseDtoList.size())
                .currentPage(page.getNumber())
                .build();
    }
}
