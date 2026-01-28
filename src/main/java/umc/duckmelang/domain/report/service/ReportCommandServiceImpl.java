package umc.duckmelang.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.chatroom.domain.ChatRoom;
import umc.duckmelang.domain.chatroom.repository.ChatRoomRepository;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.repository.PostRepository;
import umc.duckmelang.domain.report.domain.*;
import umc.duckmelang.domain.report.domain.enums.ReportStatus;
import umc.duckmelang.domain.report.domain.enums.ReportType;
import umc.duckmelang.domain.report.dto.ReportRequestDto;
import umc.duckmelang.domain.report.repository.*;
import umc.duckmelang.domain.review.domain.Review;
import umc.duckmelang.domain.review.repository.ReviewRepository;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportCommandServiceImpl implements ReportCommandService {
    private final ReportRepository reportRepository;

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final PostRepository postRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public void report( Long memberId, ReportRequestDto.reportDto dto) {
        Member member = memberRepository.findById(memberId).get();
        switch(dto.getDType()){
            case ReportType.Values.CHAT : reportChat(member,dto); break;
            case ReportType.Values.POST: reportPost(member, dto); break;
            case ReportType.Values.PROFILE : reportProfile(member, dto); break;
            case ReportType.Values.REVIEW: reportReview(member,dto); break;
        }
    }

    @Override
    public void delete(ReportRequestDto.deleteRequestDto dto) {
        List<Report> list = reportRepository.findAllById(dto.getReportIdList());

        for (Long id : dto.getReportIdList()){
            reportRepository.deleteAll(list);
        }
    }

    private void reportChat(Member sender, ReportRequestDto.reportDto dto){
        ChatRoom chatRoom = chatRoomRepository.findById(dto.getId()).get();
        Member one = chatRoom.getPost().getMember();

        ChatReport chatReport = ChatReport.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .receiver((!Objects.equals(sender.getId(), one.getId()))?one : chatRoom.getOtherMember())
                .reportStatus(ReportStatus.INTACT)
                .reason(dto.getReason())
                .build();

        reportRepository.save(chatReport);
    }

    private void reportPost(Member sender, ReportRequestDto.reportDto dto) {
        Post post = postRepository.findById(dto.getId()).get();

        PostReport postReport = PostReport.builder()
                .post(post)
                .receiver(post.getMember())
                .sender(sender)
                .reportStatus(ReportStatus.INTACT)
                .reason(dto.getReason())
                .build();

        reportRepository.save(postReport);
    }

    private void reportProfile(Member sender, ReportRequestDto.reportDto dto){
        Member receiver = memberRepository.findById(dto.getId()).get();

        ProfileReport profileReport = ProfileReport.builder()
                .introduction(receiver.getIntroduction())
                .nickname(receiver.getNickname())
                .receiver(receiver)
                .sender(sender)
                .reportStatus(ReportStatus.INTACT)
                .reason(dto.getReason())
                .build();

        reportRepository.save(profileReport);
    }

    private void reportReview(Member sender, ReportRequestDto.reportDto dto) {
        Review review = reviewRepository.findById(dto.getId()).get();

        ReviewReport reviewReport = ReviewReport.builder()
                .review(review)
                .receiver(review.getSender())
                .sender(sender)
                .reason(dto.getReason())
                .reportStatus(ReportStatus.INTACT)
                .build();

        reportRepository.save(reviewReport);
    }
}
