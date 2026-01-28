package umc.duckmelang.domain.member.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import umc.duckmelang.domain.member.domain.enums.Role;
import umc.duckmelang.global.common.BaseEntity;

import java.util.*;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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
}
