package umc.duckmelang.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.report.domain.Report;
import umc.duckmelang.domain.report.domain.enums.ReportType;
import umc.duckmelang.domain.report.dto.ReportResponseDto;
import umc.duckmelang.domain.report.repository.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportQueryServiceImpl implements ReportQueryService{
    private final ReportRepository reportRepository;
    private final PostReportRepository postReportRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final ChatReportRepository chatReportRepository;
    private final ProfileReportRepository profileReportRepository;

    private final int PAGES = 10;

    @Override
    public ReportResponseDto.ReportResponseListDto getReportResponseList(ReportType type, Integer page) {
        Page<? extends Report> reportPage;
        switch (type.name()){
            case ReportType.Values.CHAT -> {
                reportPage = chatReportRepository.findAll(PageRequest.of(page, PAGES));
            }
            case ReportType.Values.POST -> {
                reportPage = postReportRepository.findAll(PageRequest.of(page, PAGES));
            }
            case ReportType.Values.PROFILE -> {
                reportPage = profileReportRepository.findAll(PageRequest.of(page, PAGES));
            }
            case ReportType.Values.REVIEW -> {
                reportPage = reviewReportRepository.findAll(PageRequest.of(page, PAGES));
            }
            default -> {
                reportPage = reportRepository.findAll(PageRequest.of(page,PAGES));
            }
        }

        // 각 page의 신고의 신고 수, 가장 최신 신고 날짜,  가져와야함


    }
}
