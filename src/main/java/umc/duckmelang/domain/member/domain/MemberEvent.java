package umc.duckmelang.domain.member.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.duckmelang.domain.eventcategory.domain.EventCategory;
import umc.duckmelang.global.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberEvent extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_event_id")
    private Long id;

    //n:1 단방향
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_category_id")
    private EventCategory eventCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void setMember(Member member) {
        if (this.member != null) {
            this.member.getMemberEventList().remove(this);
        }
        this.member = member;
        if (member != null) {
            member.getMemberEventList().add(this);
        }
    }
}
