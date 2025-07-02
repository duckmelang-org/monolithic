package umc.duckmelang.domain.report.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import umc.duckmelang.domain.report.domain.enums.ReportType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DiscriminatorValue(ReportType.Values.PROFILE)
public class ProfileReport extends Report{
    private String introduction; // 신고 시점 introduction
    private String nickname; // 신고 시점 닉네임
}
