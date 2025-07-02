package umc.duckmelang.domain.member.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import umc.duckmelang.global.common.BaseEntity;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberProfileImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_profile_image_id")
    private Long id;

    @Column(nullable = false, length = 1024)
    private String memberImage; // S3 URL을 저장할 필드

    @Column(nullable = false)
    private boolean isPublic; // true이면 전체 공개, false이면 나만 보기

    @Column(unique = true)
    private String uuid; // 연관관계x

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void setMember(Member member) {
        if (this.member != null) {
            member.getMemberProfileImageList().remove(this);
        }
        this.member = member;
        if (member != null)
            member.getMemberProfileImageList().add(this);
    }

    // 공개설정 변경 메서드
    public void changeStatus (boolean isPublic) {
        this.isPublic = isPublic;
    }

    private static final String DEFAULT_IMAGE_URL = "default_img.url";

    /**
     * 공개된 MemberProfileImage가 없을 때 조회할 경우 사용할 디폴트 객체
     * **/
    public static MemberProfileImage createDefault(Member member) {
        return MemberProfileImage.builder()
                .member(member)
                .isPublic(true)
                .memberImage(DEFAULT_IMAGE_URL)
                .build();
    }

}
