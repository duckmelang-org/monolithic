package umc.duckmelang.domain.postidol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.repository.PostRepository;
import umc.duckmelang.domain.postidol.domain.PostIdol;

@Repository
public interface PostIdolRepository extends JpaRepository<PostIdol, Long> {
    void deleteAllByPost(Post post);
}
