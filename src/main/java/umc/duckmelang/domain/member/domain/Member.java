package umc.duckmelang.domain.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.duckmelang.domain.member.domain.type.Role;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.global.common.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Table(name = "member")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(length = 30)
    private String nickname;

    @Column(name = "loginId", unique = true)
    private String loginId;

    @Column(nullable = true, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> postList = new ArrayList<Post>();

    @Builder
    public Member(String loginId, String password, String nickname, Role role) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }
}
