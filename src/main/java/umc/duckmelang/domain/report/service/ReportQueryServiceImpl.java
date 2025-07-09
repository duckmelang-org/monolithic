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
        ReportQueryStrategy<? extends Report> strategy = strategyMap.get(type);
        return strategy.getReportResponseList(status, order, PageRequest.of(page, PAGES));
    }
}
