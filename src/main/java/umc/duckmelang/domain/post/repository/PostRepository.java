package umc.duckmelang.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.duckmelang.domain.post.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
