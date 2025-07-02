package umc.duckmelang.domain.report.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
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
    @OneToOne
    private Review review;
}
