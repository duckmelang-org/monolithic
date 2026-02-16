package umc.duckmelang.domain.application.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.duckmelang.domain.application.domain.type.ApplicationStatus;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.global.common.BaseEntity;

@Entity
@Table(name = "application")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Application extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ApplicationStatus applicationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public void updateStatus(ApplicationStatus applicationStatus){
        this.applicationStatus = applicationStatus;
    }
}
