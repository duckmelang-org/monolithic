package umc.duckmelang.domain.report.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import umc.duckmelang.domain.report.domain.enums.ReportType;
import umc.duckmelang.domain.review.domain.Review;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@DiscriminatorValue(ReportType.Values.REVIEW)
public class ReviewReport extends Report{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="review_id")
    private Review review;
}
