package umc.duckmelang.domain.bookmark.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.global.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Bookmark extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 연관관계 편의 메서드
    public void setPost(Post post) {
        if (this.post != null) {
            this.post.getBookmarkList().remove(this);
        }
        this.post = post;
        if (post != null) {
            post.getBookmarkList().add(this);
        }
    }

    public void setMember(Member member) {
        if (this.member != null) {
            this.member.getBookmarkList().remove(this);
        }
        this.member = member;
        if (member != null) {
            member.getBookmarkList().add(this);
        }
    }
}
