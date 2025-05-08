package umc.duckmelang.domain.post.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.duckmelang.domain.idolcategory.domain.IdolCategory;
import umc.duckmelang.global.common.BaseEntity;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostIdol extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_idol_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idol_category_id")
    private IdolCategory idolCategory;

    public void setPost(Post post) {
        if (this.post != null) {
            this.post.getPostIdolList().remove(this);
        }
        this.post = post;
        if (post != null)
            post.getPostIdolList().add(this);
    }

    public void setIdolCategory(IdolCategory idolCategory) {
        if (this.idolCategory != null) {
            this.idolCategory.getPostIdolList().remove(this);
        }
        this.idolCategory = idolCategory;
        if (idolCategory != null)
            idolCategory.getPostIdolList().add(this);
    }
}
