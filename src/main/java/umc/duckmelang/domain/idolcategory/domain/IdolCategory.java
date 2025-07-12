package umc.duckmelang.domain.idolcategory.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import umc.duckmelang.domain.member.domain.MemberIdol;
import umc.duckmelang.domain.post.domain.PostIdol;
import umc.duckmelang.domain.uuid.domain.Uuid;
import umc.duckmelang.global.common.BaseEntity;


import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class IdolCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idol_category_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(length = 30)
    private String company;

    @Column(length = 1024)
    private String profileImage;

    @Column(unique = true)
    private String uuid; // 연관관계x

    @OneToMany(mappedBy = "idolCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostIdol> postIdolList = new ArrayList<>();

    //n:1 단방향 고려
    @OneToMany(mappedBy = "idolCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberIdol> memberIdolList = new ArrayList<>();

    public void updateForAdmin(String newName, String newProfileImage) {
        // 비즈니스 규칙 검증
        validateName(newName);
        validateImageUrl(newProfileImage);

        this.name = newName;
        this.profileImage = newProfileImage;
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("아이돌 이름은 필수입니다");
        }
    }

    private void validateImageUrl(String imageUrl) {
        if (imageUrl == null) {
            throw new IllegalArgumentException("이미지 URL은 필수입니다");
        }
    }
}
