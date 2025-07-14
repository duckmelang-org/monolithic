package umc.duckmelang.domain.review.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import umc.duckmelang.domain.application.domain.Application;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.report.domain.ReviewReport;
import umc.duckmelang.global.common.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "DECIMAL(2,1) CHECK (score >= 0 AND score <= 5)")
    private double score;

    @Column(nullable = false, length = 500)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "member_id")
    private Member sender; // 리뷰 보내는 회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "member_id")
    private Member receiver; // 리뷰 받는 회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", referencedColumnName = "application_id", nullable = false)
    private Application application;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewReport> reviewReportList = new ArrayList<>();

    // 연관관계 편의 메서드
    public void setApplication(Application application) {
        if (this.application != null) {
            // 기존 Application의 리뷰 목록에서 제거
            this.application.removeReview(this);
        }
        this.application = application;
        if (application != null) {
            // 새로운 Application의 리뷰 목록에 추가
            application.addReview(this);
        }
    }

    public void setSender(Member sender) {
        if (this.sender != null){
            this.sender.getSentReviewList().remove(this);
        }
        this.sender = sender;
        if (sender != null)
            sender.getSentReviewList().add(this);
    }

    public void setReceiver(Member receiver) {
        if (this.receiver != null){
            this.receiver.getReceivedReviewList().remove(this);
        }
        this.receiver = receiver;
        if(receiver != null)
            receiver.getReceivedReviewList().add(this);
    }
}