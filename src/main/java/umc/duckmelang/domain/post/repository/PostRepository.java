package umc.duckmelang.domain.post.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.duckmelang.domain.post.domain.Post;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Post p WHERE p.id = :id")
    Optional<Post> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT p FROM Post p ORDER BY p.viewCount DESC")
    Page<Post> findAllOrderByViewCountDesc(Pageable pageable);
}
