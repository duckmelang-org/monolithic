package umc.duckmelang.domain.idolcategory.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.duckmelang.domain.member.domain.MemberIdol;
import umc.duckmelang.domain.post.domain.PostIdol;
import umc.duckmelang.global.common.BaseEntity;


import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
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

    @OneToMany(mappedBy = "idolCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostIdol> postIdolList = new ArrayList<>();

    //n:1 단방향 고려
    @OneToMany(mappedBy = "idolCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberIdol> memberIdolList = new ArrayList<>();
}
