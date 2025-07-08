package umc.duckmelang.domain.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.report.domain.Report;
import umc.duckmelang.domain.report.domain.enums.QueryOrder;
import umc.duckmelang.domain.report.domain.enums.ReportStatus;
import umc.duckmelang.domain.report.domain.enums.ReportType;
import umc.duckmelang.domain.report.dto.ReportResponseDto;
import umc.duckmelang.domain.report.dto.ReportSummaryDto;
import umc.duckmelang.domain.report.service.strategy.ReportQueryStrategy;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportQueryServiceImpl implements ReportQueryService{
    private final Map<ReportType, ReportQueryStrategy<? extends Report>> strategyMap;

    private final int PAGES = 10;

    public ReportQueryServiceImpl(List<ReportQueryStrategy<? extends Report>> strategyList){
        this.strategyMap = strategyList.stream()
                .collect(Collectors.toMap(
                        ReportQueryStrategy::getSupportedType,
                        Function.identity()
                ));
    }

    @Override
    public ReportResponseDto.ReportResponseListDto getReportResponseList(ReportType type, ReportStatus status, QueryOrder order, Integer page) {
        //type에 따라 다른 Repository에서 조회하는데,
        //page, status는 jpa 변수로 넘겨주고
        //order에 따라 다른 레포지토리 함수
        ReportQueryStrategy<? extends Report> strategy = strategyMap.get(type);
        Page<? extends Report> reportPage = strategy.queryReports(status, order, PageRequest.of(page, PAGES));

        // 각 page의 신고의 신고 수, 가장 최신 신고 날짜, 신고 사유  가져와야함
        List<ReportSummaryDto> list = strategyMap.getReportSummaryDtoList(reportPage);
        Map<Long, ReportSummaryDto> reportSummaryMap = list.stream()
                .collect(Collectors.toMap(ReportSummaryDto::getReportId, Function.identity()));

        return strategyMap.convertToResponseList(reportPage,reportSummaryMap);
    }
}
