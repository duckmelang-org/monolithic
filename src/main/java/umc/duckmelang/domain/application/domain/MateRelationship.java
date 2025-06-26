package umc.duckmelang.domain.application.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.global.common.BaseEntity;



@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MateRelationship extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mate_relationship_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_member_id", referencedColumnName = "member_id", nullable = false)
    private Member firstMember; // 첫 번째 회원

    @ManyToOne
    @JoinColumn(name = "second_member_id", referencedColumnName = "member_id", nullable = false)
    private Member secondMember; // 두 번째 회원

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application; // 관련 신청 ID (다른 테이블과 연관 가능)

    public void setFirstMember(Member firstMember) {
        if (this.firstMember != null) {
            this.firstMember.getMateRelationshipinFirstList().remove(this);
        }
        this.firstMember = firstMember;
        if (firstMember != null)
            firstMember.getMateRelationshipinFirstList().add(this);
    }

    public void setSecondMember(Member secondMember) {
        if (this.secondMember != null) {
            this.secondMember.getMateRelationshipinSecondList().remove(this);
        }
        this.secondMember = secondMember;
        if (secondMember != null)
            secondMember.getMateRelationshipinSecondList().add(this);
    }

    public void setApplication(Application application) {
        if (this.application != null) {
            this.application.setMateRelationship(null);
        }
        this.application = application;
        if (application != null) {
            application.setMateRelationship(this);
        }
    }
}

