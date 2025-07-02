package umc.duckmelang.domain.report.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.report.domain.enums.ReportType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@DiscriminatorValue(ReportType.Values.POST)
public class PostReport extends Report{
    @OneToOne
    private Post post;
}
