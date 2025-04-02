package umc.duckmelang.domain.member.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.duckmelang.domain.idolcategory.domain.IdolCategory;
import umc.duckmelang.global.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberIdol extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_idol_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idol_category_id")
    private IdolCategory idolCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

//    public void setIdolCategory(IdolCategory idolCategory) {
//        if (this.idolCategory != null) {
//            this.idolCategory.getMemberIdolList().remove(this);
//        }
//        this.idolCategory = idolCategory;
//        this.idolCategory.getMemberIdolList().add(this);
//    }

    public void setMember(Member member) {
        if (this.member != null) {
            this.member.getMemberIdolList().remove(this);
        }
        this.member = member;
        if (member != null)
            this.member.getMemberIdolList().add(this);
    }
}
